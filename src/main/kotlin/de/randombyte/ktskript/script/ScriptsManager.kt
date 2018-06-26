package de.randombyte.ktskript.script

import de.randombyte.ktskript.utils.KtSkript
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner
import java.io.File
import java.nio.file.Path
import javax.script.CompiledScript
import javax.script.ScriptException

class ScriptsManager {
    companion object {
        fun generateHelpers(script: Script) =
                """
                    val script = ${script.toCode()}
                """.trimIndent()
    }

    class InternalScript(val path: Path, val compiledScript: CompiledScript)
    val scripts: MutableMap<String, InternalScript> = mutableMapOf()

    private val allClasspathFiles = lazy { getAsMuchClasspathAsPossible().map { it.absoluteFile } }

    private fun newEngine(templateClasspath: List<File>) = MyKotlinJsr223JvmLocalScriptEngineFactory(templateClasspath).scriptEngine

    var globalImports = ""

    fun clear() {
        scripts.clear()
        globalImports = ""
    }

    /**
     * @return Import statements for all classes from the packages specified in the given [file]
     */
    fun loadImportsFromFile(file: File): String {
        val packagePrefixes = file
                .readLines()
                .filter { it.isNotBlank() }
                .toTypedArray()

        val classPathImportPackages = FastClasspathScanner(*packagePrefixes)
                .overrideClasspath(allClasspathFiles.value)
                .alwaysScanClasspathElementRoot()
                .scan()
                .namesOfAllClasses
                .map { it.substringBeforeLast(".") } // snip away class name
                .filter { it.isNotEmpty() }
                .toSet() // ensure uniqueness

        val newImports = classPathImportPackages
                .joinToString(separator = "\n") { "import $it.*;" }

        return newImports
    }

    /**
     * Loads all classes from the packages specified in the given [path] which should point to the
     * "default.imports" file.
     *
     * @param path [Path] to the root config dir
     */
    fun loadGlobalImports(path: Path) {
        globalImports += loadImportsFromFile(path.resolve("default.imports").toFile())
    }

    /**
     * Loads all classes from the packages specified in the given [file] which should point to the
     * script specific ".imports" file.
     *
     * @param file [Path] to the ".imports" file
     *
     * @return all import statements from the file
     */
    fun loadScriptSpecificImports(file: File): String = loadImportsFromFile(file)

    /**
     * @return the successfully read scripts
     */
    fun loadScripts(path: Path): Map<String, InternalScript> {
        val scriptFiles = getFiles(path, extension = "ktskript")
                .map { it.nameWithoutExtension to it }
                .toList()

        // check duplicate use of IDs
        val duplicatedIds = scriptFiles
                .groupBy { (id, _) -> id }
                .mapValues { (_, occurrences) -> occurrences.count() }
                .filter { (_, count) -> count > 1 }

        duplicatedIds.forEach { (id, count) ->
            KtSkript.logger.error("Ignoring scripts '$id': Multiple use of script ID '$id'($count times)!")
        }
        if (duplicatedIds.isNotEmpty()) return emptyMap()

        // put imports into script and compile everything
        val compiledScripts = scriptFiles
                .toMap() // uniqueness of keys is now guaranteed
                .filter { (id, file) ->
                    if (id in scripts.keys) {
                        KtSkript.logger.warn("Ignoring already used script ID '$id' at '${file.absolutePath}'!")
                        false
                    } else true
                }
                .map { (id, file) -> Triple(id, file, file.readText()) }
                .map { (id, file, scriptContent) ->

                    val importsFile = File(file.nameWithoutExtension + ".imports")
                    val scriptSpecificImports: String? = if (importsFile.exists() && importsFile.isFile) {
                        loadScriptSpecificImports(importsFile)
                    } else null

                    val scriptString = """
                        $globalImports
                        ${scriptSpecificImports.orEmpty()}
                        $scriptContent
                        ${generateHelpers(Script(path))}
                    """.trimIndent()

                    if (KtSkript.configAccessors.general.get().outputScripts) {
                        KtSkript.logger.info("Script '$id':\n$scriptString")
                    }

                    Triple(id, file, scriptString)
                }
                .mapNotNull { (id, file, scriptString) ->
                    val compiledScript = try {
                        // reset engine because there might be errors from previous scripts
                        val scriptEngine = newEngine(allClasspathFiles.value)
                        scriptEngine.compile(scriptString)
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
     * to the script engine being dumb about errors.
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