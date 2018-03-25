package de.randombyte.ktskript

import de.randombyte.ktskript.extensions.KtSkript
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngineFactory
import java.io.File
import javax.script.CompiledScript
import javax.script.ScriptException

class ScriptsManager {
    companion object {
        val IMPORTS =
                """
                    import de.randombyte.ktskript.extensions.*;
                    import de.randombyte.ktskript.extensions.messages.*;

                    import org.spongepowered.api.command.args.GenericArguments.*;
                    import org.spongepowered.api.data.key.Keys.*;
                    import org.spongepowered.api.text.chat.ChatTypes.*;

                    import org.spongepowered.api.entity.living.player.*;
                """.trimIndent()
    }

    private val scriptEngine = KotlinJsr223JvmLocalScriptEngineFactory().scriptEngine as KotlinJsr223JvmLocalScriptEngine

    val scripts: MutableMap<String, CompiledScript> = mutableMapOf()

    fun clear() {
        scriptEngine.state.history.reset()
        scripts.clear()
    }

    /**
     * @return the successfully read scripts
     */
    fun loadFromPath(path: File): Map<String, CompiledScript> {
        val compiledScripts = path.walk()
                .filter { file ->
                    if (file.isDirectory) return@filter false
                    if (file.extension == "ktskript") true else {
                        KtSkript.logger.warn("Ignoring file '${file.absolutePath}'!")
                        false
                    }
                }
                .map { it.nameWithoutExtension to it }
                .filter { (id, file) ->
                    if (!scripts.containsKey(id)) true else {
                        KtSkript.logger.warn("Ignoring already used script id '$id' at '${file.absolutePath}'!")
                        false
                    }
                }
                .map { (id, file) -> Triple(id, file, file.readText()) }
                .map { (id, file, content) -> Triple(id, file, "$IMPORTS\n$content") }
                .mapNotNull { (id, file, content) ->
                    val compiledScript = try {
                        scriptEngine.compile(content)
                    } catch (ex: ScriptException) {
                        KtSkript.logger.error("Ignoring faulty script '$id' at '${file.absolutePath}'!")
                        ex.printStackTrace()
                        return@mapNotNull null
                    }
                    id to compiledScript
                }
                .toMap()

        scripts += compiledScripts
        return compiledScripts
    }

    /**
     * @see runScriptsSafely
     */
    fun runAllScriptsSafely() = runScriptsSafely(*scripts.keys.toTypedArray())

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
            script.eval()
        } catch (ex: ScriptException) {
            KtSkript.logger.error("Can not run script '$id'!", ex)
            scripts.remove(id)
            return false
        }
        return true
    }
}