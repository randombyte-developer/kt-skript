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

    private lateinit var task: Task
    private lateinit var watchService: WatchService

    init {
        setupTask()
    }

    fun setupTask () {
        watchService = FileSystems.getDefault().newWatchService()
        path.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY)

        task = Task.builder()
                .interval(interval.toMillis(), TimeUnit.MILLISECONDS)
                .execute { ->
                    checkForChanges()
                }
                .submit(KtSkript)
    }

    private fun checkForChanges() {
        val watchKey = watchService.poll() ?: return
        if (watchKey.pollEvents().isNotEmpty()) {
            onChanges()
        }
        watchKey.reset()
    }

    fun stop() {
        task.cancel()
    }
}