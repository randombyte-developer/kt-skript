package de.randombyte.ktskript

import de.randombyte.ktskript.utils.KtSkript
import org.spongepowered.api.scheduler.Task
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds.*
import java.nio.file.WatchService
import java.time.Duration
import java.util.concurrent.TimeUnit

class FolderWatcher(val path: Path, val interval: Duration, val onChanges: () -> Unit) {

    companion object {
        const val SCRIPTS_FOLDER_WATCHER_TASK_NAME = "kt-skript-scripts-folder-watcher-task"
    }

    private lateinit var task: Task
    private lateinit var watchService: WatchService

    init {
        setupTask()
    }

    private fun setupTask () {
        watchService = FileSystems.getDefault().newWatchService()
        path.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY)

        task = Task.builder()
                .name(SCRIPTS_FOLDER_WATCHER_TASK_NAME)
                .interval(interval.toMillis(), TimeUnit.MILLISECONDS)
                .execute { ->
                    checkForChanges()
                }
                .submit(KtSkript)
    }

    private fun checkForChanges() {
        val watchKey = watchService.poll() ?: return
        if (watchKey.pollEvents().isNotEmpty()) {
            try {
                onChanges()
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
            }
        }

        watchKey.reset()
    }

    fun stop() {
        task.cancel()
    }
}