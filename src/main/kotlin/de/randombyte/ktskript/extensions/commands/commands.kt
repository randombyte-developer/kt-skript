package de.randombyte.ktskript.extensions.commands

import de.randombyte.ktskript.extensions.CommandManager
import de.randombyte.ktskript.extensions.KtSkript
import de.randombyte.ktskript.extensions.t
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player

fun command(vararg names: String, commandBuilder: CommandSpec.Builder.() -> Unit) {
    val builder = CommandSpec.builder()
    commandBuilder.invoke(builder)
    CommandManager.register(KtSkript, builder.build(), *names)
}

fun CommandSpec.Builder.action(onlyPlayers: Boolean = false, executor: CommandExecutorContext.() -> Any) {
    executor { src, args ->
        if (onlyPlayers && src !is Player) throw CommandException("Command must be executed by a player!".t)
        val result = executor(CommandExecutorContext(src, args))
        return@executor result as? CommandResult ?: CommandResult.success()
    }
}

data class CommandExecutorContext(val commandSource: CommandSource, val arguments: CommandContext) {
    val player: Player
        get() {
            if (commandSource !is Player) {
                throw CommandException("The command source is not a player! Use 'executor(onlyPlayers = true)'.".t)
            }
            return commandSource
        }

    fun <T> argument(key: String): T = arguments.getOne<T>(key).orElseThrow {
        CommandException("Argument '$key' not present!".t)
    }
}