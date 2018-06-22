package de.randombyte.ktskript

import de.randombyte.ktskript.utils.KtSkript
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngineFactory
import org.spongepowered.api.Sponge
import java.nio.file.Path
import javax.script.CompiledScript
import javax.script.ScriptException

class ScriptsManager {
    companion object {
        val importPackages = arrayOf("org.spongepowered.api")

        fun generateHelpers(script: Script) =
                """
                    val script = ${script.toCode()}
                """.trimIndent()
    }

    private var scriptEngine = newEngine()

    var imports = ""

    class InternalScript(val path: Path, val compiledScript: CompiledScript)
    val scripts: MutableMap<String, InternalScript> = mutableMapOf()

    private fun newEngine() = KotlinJsr223JvmLocalScriptEngineFactory().scriptEngine as KotlinJsr223JvmLocalScriptEngine

    fun clear() {
        scripts.clear()
        imports = ""
    }

    fun loadImports(path: Path) {
        val classPathImportPackages = FastClasspathScanner(*importPackages)
                .addClassLoader(Sponge::class.java.classLoader)
                .scan()
                .namesOfAllClasses
                .map { it.substringBeforeLast(".") }
                .filter { it.isNotEmpty() }

        val fileImportPackages = getFiles(path, extension = "imports")
                .flatMap { it.readLines().filter(String::isNotBlank).asSequence() }

        val newImports = (classPathImportPackages.toSet() + fileImportPackages)
                .joinToString(separator = "\n") { "import $it.*;" }

        imports += newImports
    }

    /**
     * @return the successfully read scripts
     */
    fun loadScripts(path: Path): Map<String, InternalScript> {
        val scriptFiles = getFiles(path, extension = "ktskript")
                .map { it.nameWithoutExtension to it }
                .toMap() // this call actually prevents strange double code executions in the filter closure

        // check duplicate use of IDs
        val scriptIdOccurrences = mutableMapOf<String, Int>()
        scriptFiles.forEach { (id, _) -> scriptIdOccurrences[id] = (scriptIdOccurrences[id] ?: 0) + 1 }
        val duplicatedIds = scriptIdOccurrences.filter { (_, count) -> count > 1 }
        duplicatedIds.forEach { (id, count) ->
            KtSkript.logger.error("Ignoring scripts '$id': Multiple use of script ID '$id'($count times)!")
        }
        if (duplicatedIds.isNotEmpty()) return emptyMap()

        val compiledScripts =
                scriptFiles.filter { (id, file) ->
                    if (!scripts.containsKey(id)) true else {
                        KtSkript.logger.warn("Ignoring already used script ID '$id' at '${file.absolutePath}'!")
                        false
                    }
                }
                .map { (id, file) -> Triple(id, file, file.readText()) }
                .map { (id, file, content) ->

                    val scriptString = """
                        $imports
                        $content
                        ${generateHelpers(Script(path))}
                    """.trimIndent()

                    Triple(id, file, scriptString)
                }
                .mapNotNull { (id, file, content) ->
                    val compiledScript = try {
                        scriptEngine = newEngine() // reset engine because there might be errors from previous scripts
                        scriptEngine.compile(content)
                    } catch (ex: ScriptException) {
                        KtSkript.logger.error("Ignoring faulty script '$id' at '${file.absolutePath}'!")
                        ex.printStackTrace()
                        return@mapNotNull null
                    }
                    id to InternalScript(file.toPath(), compiledScript)
                }
                .toMap()

        scripts += compiledScripts
        return compiledScripts
    }

    /**
     * This tries to run all scripts. If any script fails to execute, it is removed from [scripts]
     * and the remaining script are then again tried to be executed, until all were tried. This is due
     * to the script enging
     *
     * @see runScriptsSafely
     */
    fun runAllScriptsSafely() {
        do {
            val result = runScriptsSafely(*scripts.keys.toTypedArray())
        } while (result.values.any { success -> !success })
    }

    /**
     * @return if the specific scripts were run successfully
     */
    fun runScriptsSafely(vararg ids: String) = ids.map { id -> id to runScriptSafely(id) }.toMap()

    /**
     * Tries to run the script with the given [id]. If the execution fails the script will be removed
     * from the [scripts].
     *
     * @return if the script was run successfully
     */
    fun runScriptSafely(id: String): Boolean {
        val script = scripts[id] ?: throw IllegalArgumentException("No script available with id '$id'!")
        try {
            script.compiledScript.eval()
        } catch (ex: ScriptException) {
            KtSkript.logger.error("Can not run script '$id'!", ex)
            scripts.remove(id)
            return false
        }
        return true
    }

    private fun getFiles(path: Path, extension: String) = path.toFile().walk().filter { file ->
        if (file.isDirectory) return@filter false
        if (file.extension == extension) true else {
            KtSkript.logger.warn("Ignoring file '${file.absolutePath}'!")
            false
        }
    }
}