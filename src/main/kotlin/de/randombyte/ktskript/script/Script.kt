package de.randombyte.ktskript.script

import de.randombyte.ktskript.script.ScriptsManager.InternalScript
import de.randombyte.ktskript.utils.KtSkript
import java.nio.file.Path

class Script(val path: Path) {
    fun toCode() = """
        Script(Paths.get("${path.toAbsolutePath()}"))
    """.trimIndent()

    fun compile(path: Path): Map<String, InternalScript> = KtSkript.scriptsManager.loadScripts(path)
}