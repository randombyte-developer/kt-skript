package de.randombyte.ktskript.script

import de.randombyte.ktskript.script.ScriptsManager.InternalScript
import de.randombyte.ktskript.utils.KtSkript
import java.nio.file.Path

class Script(val id: String, val path: Path) {

    companion object {
        const val THREE_QUOTES = "\"\"\""
    }

    // This could be implemented with bindings but it seems to be more readable for now
    fun toCode() = """
        KtSkriptScript("$id", Paths.get($THREE_QUOTES${path.toAbsolutePath()}$THREE_QUOTES))
    """.trimIndent()

    /**
     * Compiles one or more scripts.
     * If [path] points to a single file, that one is tried to be compiled.
     * If [path] points to a folder, every .ktskript file is tried.
     * If a .ktskript file was already compiled (and is now cached), it won't be recompiled.
     *
     * @return a [Map] which consists of the successfully compiled and/or cached [InternalScript]s mapped to its ID
     */
    fun compile(path: Path): Map<String, InternalScript> = KtSkript.scriptsManager.loadScripts(path)
}