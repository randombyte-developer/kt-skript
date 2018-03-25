package de.randombyte.ktskript

import com.google.inject.Inject
import de.randombyte.ktskript.KtSkript.Companion.AUTHOR
import de.randombyte.ktskript.KtSkript.Companion.ID
import de.randombyte.ktskript.KtSkript.Companion.NAME
import de.randombyte.ktskript.KtSkript.Companion.VERSION
import de.randombyte.ktskript.extensions.CommandManager
import org.slf4j.Logger
import org.spongepowered.api.config.ConfigDir
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.GameReloadEvent
import org.spongepowered.api.event.game.state.GameInitializationEvent
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.plugin.PluginContainer
import java.nio.file.Files
import java.nio.file.Path

@Plugin(
        id = ID,
        name = NAME,
        version = VERSION,
        authors = [AUTHOR])
class KtSkript @Inject constructor(
        val logger: Logger,
        @ConfigDir(sharedRoot = false) private val configPath: Path,
        //private val bStats: Metrics,
        private val pluginContainer: PluginContainer
) {

    companion object {
        const val ID = "ktskript"
        const val NAME = "KtSkript"
        const val VERSION = "1.0"
        const val AUTHOR = "RandomByte"
    }

    private val scriptDir: Path = configPath.resolve("scripts")

    init {
        if (Files.notExists(configPath)) Files.createDirectory(configPath)
        if (Files.notExists(scriptDir)) Files.createDirectory(scriptDir)
    }

    private val scriptsManager = ScriptsManager()

    @Listener
    fun onInit(event: GameInitializationEvent) {
        reloadAllScripts()

        logger.info("Loaded $NAME: $VERSION")
    }

    @Listener
    fun onReload(event: GameReloadEvent) {
        reloadAllScripts()

        logger.info("Reloaded!")
    }

    private fun reloadAllScripts() {
        CommandManager.getOwnedBy(this).map(CommandManager::removeMapping)

        scriptsManager.clear()
        scriptsManager.loadFromPath(scriptDir.toFile())
        scriptsManager.runAllScriptsSafely()

        logger.info("Loaded ${scriptsManager.scripts.size} script(s).")
    }
}