package de.randombyte.ktskript

import com.google.inject.Inject
import de.randombyte.ktskript.KtSkriptPlugin.Companion.AUTHOR
import de.randombyte.ktskript.KtSkriptPlugin.Companion.ID
import de.randombyte.ktskript.KtSkriptPlugin.Companion.NAME
import de.randombyte.ktskript.KtSkriptPlugin.Companion.VERSION
import de.randombyte.ktskript.config.ConfigAccessors
import de.randombyte.ktskript.script.ScriptsManager
import de.randombyte.ktskript.script.UnloadScriptsEvent
import de.randombyte.ktskript.utils.*
import org.apache.commons.lang3.RandomUtils
import org.bstats.sponge.Metrics
import org.slf4j.Logger
import org.spongepowered.api.Sponge
import org.spongepowered.api.config.ConfigDir
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.cause.EventContext
import org.spongepowered.api.event.game.GameReloadEvent
import org.spongepowered.api.event.game.state.GameInitializationEvent
import org.spongepowered.api.event.game.state.GameStoppingServerEvent
import org.spongepowered.api.event.network.ClientConnectionEvent
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.plugin.PluginContainer
import org.spongepowered.api.scheduler.Task
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.concurrent.TimeUnit

@Plugin(
        id = ID,
        name = NAME,
        version = VERSION,
        authors = [AUTHOR])
class KtSkriptPlugin @Inject constructor(
        val logger: Logger,
        @ConfigDir(sharedRoot = false) private val configPath: Path,
        private val bStats: Metrics,
        private val pluginContainer: PluginContainer
) {

    companion object {
        const val ID = "kt-skript"
        const val NAME = "KtSkript"
        const val VERSION = "1.3.0"
        const val AUTHOR = "RandomByte"

        const val DEFAULT_IMPORTS_FILE_NAME = "default.imports"
    }

    val scriptsManager = ScriptsManager()

    val configAccessors = ConfigAccessors(configPath)

    val scriptDir = configPath.resolve("scripts")

    val cause = Cause.builder().append(this).build(EventContext.empty())

    init {
        if (Files.notExists(scriptDir)) Files.createDirectory(scriptDir)
    }

    private var scriptsWatcher: FolderWatcher? = null

    @Listener
    fun onInit(event: GameInitializationEvent) {
        System.setProperty("idea.use.native.fs.for.win", "false")

        reload()

        if (needsMotivationalSpeech()) {
            Task.builder()
                    .delay(RandomUtils.nextLong(80, 130), TimeUnit.SECONDS)
                    .execute { -> Texts.motivationalSpeech.forEach { it.sendTo(Sponge.getServer().console) } }
                    .submit(this)
        }

        logger.info("Loaded $NAME: $VERSION")
    }

    @Listener
    fun onReload(event: GameReloadEvent) {
        reload()
        logger.info("Reloaded!")
    }

    @Listener
    fun onStopping(event: GameStoppingServerEvent) {
        postUnloadScriptsEvent()
    }

    private fun reload() {
        configAccessors.reloadAll()
        copyDefaultImportsIfNeeded()
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

    /**
     * Unregisters mostly everything which was set up by scripts, and by this plugin as well
     */
    private fun tidyUp() {
        CommandManager.getOwnedBy(this).map(CommandManager::removeMapping)
        EventManager.unregisterPluginListeners(this)
        Scheduler.getScheduledTasks(this)
                .filterNot { it.name == FolderWatcher.SCRIPTS_FOLDER_WATCHER_TASK_NAME }
                .forEach { it.cancel() }
    }

    private fun reloadAllScripts() {
        // Notify scripts of them being about to be unloaded to allow them to save configs
        postUnloadScriptsEvent()

        // removes all left over event handlers, commands etc. of scripts
        tidyUp()

        try {
            with(scriptsManager) {
                clear()
                loadGlobalImports(configPath, verbose = configAccessors.general.get().verboseClasspathScanner)
                loadScripts(scriptDir)
                runAllScriptsSafely()
            }
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }

        // Register our own reload event listener again
        EventManager.registerListeners(this, this)

        logger.info("Loaded ${scriptsManager.scripts.size} script(s).")
    }

    private fun postUnloadScriptsEvent() {
        EventManager.post(UnloadScriptsEvent(scriptsManager.scripts.keys.toList(), cause))
    }

    /**
     * Rewrites the default.imports file to match the built-in one, only if the config option is
     * set accordingly.
     */
    private fun copyDefaultImportsIfNeeded() {
        if (!configAccessors.general.get().allowDefaultImportsModifications) {
            pluginContainer.getAsset(DEFAULT_IMPORTS_FILE_NAME).get().copyToDirectory(configPath, true)
        }
    }


    val metricsNoteSent = mutableSetOf<UUID>()

    @Listener
    fun onPlayerJoin(event: ClientConnectionEvent.Join) {
        val uuid = event.targetEntity.uniqueId
        if (needsMotivationalSpeech(event.targetEntity)) {
            Task.builder()
                    .delay(RandomUtils.nextLong(10, 50), TimeUnit.SECONDS)
                    .execute { ->
                        val player = uuid.getPlayer() ?: return@execute
                        metricsNoteSent += uuid
                        Texts.motivationalSpeech.forEach { it.sendTo(player) }
                    }
                    .submit(this)
        }
    }

    private fun needsMotivationalSpeech(player: Player? = null) = configAccessors.general.get().enableMetricsMessages &&
            !Sponge.getMetricsConfigManager().areMetricsEnabled(this) &&
            ((player == null) || player.uniqueId !in metricsNoteSent && player.hasPermission("nucleus.mute.base")) // also passes OPs without Nucleus
}