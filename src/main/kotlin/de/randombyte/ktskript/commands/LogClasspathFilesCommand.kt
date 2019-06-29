package de.randombyte.ktskript.commands

import de.randombyte.ktskript.utils.KtSkript
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor

class LogClasspathFilesCommand : CommandExecutor {
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        KtSkript.logger.info("========== Classpath files ==========")
        KtSkript.scriptsManager.allClasspathFiles.value.forEach {
            KtSkript.logger.info(it.absolutePath)
        }

        return CommandResult.success()
    }
}