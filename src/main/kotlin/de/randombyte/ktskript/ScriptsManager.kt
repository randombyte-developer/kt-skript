package de.randombyte.ktskript

import de.randombyte.ktskript.extensions.KtSkript
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngineFactory
import java.io.File
import java.nio.file.Path
import javax.script.CompiledScript
import javax.script.ScriptException

class ScriptsManager {
    companion object {
        val IMPORTS =
                """
                    import de.randombyte.ktskript.extensions.*;
                    import de.randombyte.ktskript.extensions.particles.*;
                    import de.randombyte.ktskript.extensions.events.*;
                    import de.randombyte.ktskript.extensions.commands.*;

                    import org.spongepowered.api.command.args.GenericArguments.*;
                    import org.spongepowered.api.data.key.Keys.*;
                    import org.spongepowered.api.text.chat.ChatTypes.*;
                    import org.spongepowered.api.effect.particle.ParticleTypes.*;
                    import org.spongepowered.api.util.Color.*;
                    import org.spongepowered.api.item.FireworkShapes.*;
                    import org.spongepowered.api.block.BlockTypes.*;
                    import org.spongepowered.api.entity.EntityTypes.*;

                    import org.spongepowered.api.entity.living.player.*;
                """.trimIndent()

        fun generateHelpers(scriptPath: Path) =
                """
                    import java.nio.file.Paths;

                    val scriptPath = Paths.get("${scriptPath.toAbsolutePath()}")
                """.trimIndent()
    }

    private var scriptEngine = newEngine()

    val scripts: MutableMap<String, CompiledScript> = mutableMapOf()

    private fun newEngine() = KotlinJsr223JvmLocalScriptEngineFactory().scriptEngine as KotlinJsr223JvmLocalScriptEngine

    fun clear() {
        scriptEngine = newEngine()
        scripts.clear()
    }

    /**
     * @return the successfully read scripts
     */
    fun loadFromPath(path: File): Map<String, CompiledScript> {
        val scriptFiles = path.walk()
                .filter { file ->
                    if (file.isDirectory) return@filter false
                    if (file.extension == "ktskript") true else {
                        KtSkript.logger.warn("Ignoring file '${file.absolutePath}'!")
                        false
                    }
                }
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
                    val helpers = generateHelpers(file.toPath())
                    Triple(id, file, "$IMPORTS\n$helpers\n$content")
                }
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