package de.randombyte.ktskript.script

import org.jetbrains.kotlin.cli.common.repl.KotlinJsr223JvmScriptEngineFactoryBase
import org.jetbrains.kotlin.cli.common.repl.ScriptArgsWithTypes
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import org.jetbrains.kotlin.script.jsr223.KotlinStandardJsr223ScriptTemplate
import java.io.File
import javax.script.Bindings
import javax.script.ScriptContext

// Taken from the KotlinJsr223ScriptEngineFactoryExamples.kt

class MyKotlinJsr223JvmLocalScriptEngineFactory(private val templateClasspath: List<File>) : KotlinJsr223JvmScriptEngineFactoryBase() {
    override fun getScriptEngine() = KotlinJsr223JvmLocalScriptEngine(
            this,
            templateClasspath,
            KotlinStandardJsr223ScriptTemplate::class.qualifiedName!!,
            { ctx, types -> ScriptArgsWithTypes(arrayOf(ctx.getBindings(ScriptContext.ENGINE_SCOPE)), types ?: emptyArray()) },
            arrayOf(Bindings::class)
    )
}