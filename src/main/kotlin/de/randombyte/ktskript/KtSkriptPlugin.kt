package de.randombyte.ktskript

import com.google.inject.Inject
import de.randombyte.ktskript.KtSkriptPlugin.Companion.AUTHOR
import de.randombyte.ktskript.KtSkriptPlugin.Companion.ID
import de.randombyte.ktskript.KtSkriptPlugin.Companion.NAME
import de.randombyte.ktskript.KtSkriptPlugin.Companion.VERSION
import de.randombyte.ktskript.config.ConfigAccessors
import de.randombyte.ktskript.script.ScriptsManager
import de.randombyte.ktskript.utils.CommandManager
import de.randombyte.ktskript.utils.EventManager
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
class KtSkriptPlugin @Inject constructor(
        val logger: Logger,
        @ConfigDir(sharedRoot = false) private val configPath: Path,
        //private val bStats: Metrics,
        private val pluginContainer: PluginContainer
) {

    companion object {
        const val ID = "kt-skript"
        const val NAME = "KtSkript"
        const val VERSION = "1.0"
        const val AUTHOR = "RandomByte"

        const val DEFAULT_IMPORTS_FILE_NAME = "default.imports"
    }

    private val scriptsManager = ScriptsManager()

    val configAccessors = ConfigAccessors(configPath)

    val scriptDir = configPath.resolve("scripts")

    init {
        if (Files.notExists(scriptDir)) Files.createDirectory(scriptDir)
    }

    private var scriptsWatcher: FolderWatcher? = null

    @Listener
    fun onInit(event: GameInitializationEvent) {
        reload()
        logger.info("Loaded $NAME: $VERSION")
    }

    @Listener
    fun onReload(event: GameReloadEvent) {
        reload()
        logger.info("Reloaded!")
    }

    private fun reload() {
        copyDefaultImportsIfNecessary()
        configAccessors.reloadAll()
        reloadAllScripts()
        setupFolderWatchers()
    }

    private fun setupFolderWatchers() {
        scriptsWatcher?.stop()
        scriptsWatcher = FolderWatcher(scriptDir, configAccessors.general.get().updateInterval) {
            logger.info("Detected changes in the scripts folder.")
            reloadAllScripts()
        }
    }

    private fun reloadAllScripts() {
        CommandManager.getOwnedBy(this).map(CommandManager::removeMapping)
        EventManager.unregisterPluginListeners(this)

        try {
            scriptsManager.clear()
            scriptsManager.loadGlobalImports(configPath)
            scriptsManager.loadScripts(scriptDir)
            scriptsManager.runAllScriptsSafely()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        EventManager.registerListeners(this, this)

        logger.info("Loaded ${scriptsManager.scripts.size} script(s).")
    }

    private fun copyDefaultImportsIfNecessary() {
        pluginContainer.getAsset(DEFAULT_IMPORTS_FILE_NAME).get().copyToDirectory(configPath, false, true)
    }
}