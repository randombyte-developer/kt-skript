package de.randombyte.ktskript.utils.commands

import de.randombyte.ktskript.utils.CommandManager
import org.spongepowered.api.command.CommandSource

fun CommandSource.executeCommand(command: String) = CommandManager.process(this, command)
fun List<CommandSource>.executeCommand(command: String) = forEach { it.executeCommand(command) }