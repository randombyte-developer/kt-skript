package de.randombyte.ktskript

import java.nio.file.Path

class Script(val path: Path) {
    fun toCode() = """
        Script(Paths.get("${path.toAbsolutePath()}"))
    """.trimIndent()
}