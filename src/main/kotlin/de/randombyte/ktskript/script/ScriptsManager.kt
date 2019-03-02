package de.randombyte.ktskript.script

import de.randombyte.ktskript.utils.KtSkript
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult
import java.io.File
import java.nio.file.Path
import javax.script.CompiledScript

class ScriptsManager {
    companion object {
        // doing the typealias thing here because 'Script' is apparently already used somewhere in the imported packages
        fun generateHelpers(script: Script) =
                """
                    typealias KtSkriptScript = de.randombyte.ktskript.script.Script
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
    fun scanClasspath(wantedImports: List<String>, verbose: Boolean): ScanResult =
            FastClasspathScanner(*wantedImports.toTypedArray())
                    .verbose(verbose)
                    .overrideClasspath(allClasspathFiles.value)
                    .alwaysScanClasspathElementRoot(false)
                    .strictWhitelist()
                    .scan()

    fun readWantedImports(file: File) = file
            .readLines()
            .filter { it.isNotBlank() }

    fun generateImportsFromScanResult(scanResult: ScanResult): String {
        val classPathImportPackages = scanResult
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
    fun loadGlobalImports(path: Path, verbose: Boolean) {
        globalImports += generateImportsFromScanResult(scanClasspath(readWantedImports(path.resolve("default.imports").toFile()), verbose))
    }

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

        val alreadyCompiledScripts = mutableMapOf<String, InternalScript>()

        val generalConfig = KtSkript.configAccessors.general.get()

        val compiledScripts = scriptFiles
                .toMap() // uniqueness of keys is now guaranteed
                // filter already used script IDs
                .filter { (id, file) ->
                    if (id in scripts.keys) {
                        if (generalConfig.warnAboutDuplicates) {
                            KtSkript.logger.warn("Ignoring already used script ID '$id' at '${file.absolutePath}'!")
                        }
                        alreadyCompiledScripts += id to scripts.getValue(id)
                        false
                    } else true
                }
                // read script file
                .map { (id, file) -> Triple(id, file, file.readText()) }
                // add imports and the Script helper object
                .map { (id, file, scriptContent) ->

                    val importsFile = File(file.parent, file.nameWithoutExtension + ".imports")
                    val classpathScanResult = if (importsFile.exists() && importsFile.isFile) {
                        scanClasspath(readWantedImports(importsFile), verbose = generalConfig.verboseClasspathScanner)
                    } else null

                    val scriptSpecificImports = classpathScanResult?.let { generateImportsFromScanResult(it) }

                    val scriptString = """
                        $globalImports
                        ${scriptSpecificImports.orEmpty()}
                        ${generateHelpers(Script(id, file.toPath()))}
                        $scriptContent
                    """.trimIndent()

                    if (generalConfig.outputScripts) {
                        KtSkript.logger.info("Script '$id':\n$scriptString")
                    }

                    Quadruple(id, file, scriptString)
                }
                // compile script
                .mapNotNull { (id, file, scriptString) ->
                    val compiledScript = try {
                        // reset engine because there might be errors from previous scripts
                        val scriptEngine = newEngine(getClasspathFilesForScript())
                        scriptEngine.compile(scriptString)
                    } catch (throwable: Throwable) {
                        KtSkript.logger.error("Ignoring faulty script '$id' at '${file.absolutePath}'!", throwable)
                        return@mapNotNull null
                    }
                    id to InternalScript(file.toPath(), compiledScript)
                }
                .toMap()

        scripts += compiledScripts

        return compiledScripts + alreadyCompiledScripts
    }

    /**
     * This tries to run all scripts. If any script fails to execute, it is removed from [scripts].
     */
    fun runAllScriptsSafely() {
        runScriptsSafely(*scripts.keys.toTypedArray())
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
        } catch (throwable: Throwable) {
            KtSkript.logger.error("Can not run script '$id'!", throwable)
            scripts.remove(id)
            return false
        }
        return true
    }

    private fun getFiles(path: Path, extension: String) = path.toFile().walk().filter { file ->
        file.isFile && file.extension == extension
    }
}