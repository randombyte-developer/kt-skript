package de.randombyte.ktskript.extensions

import org.spongepowered.api.Server
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandManager
import org.spongepowered.api.event.EventManager
import org.spongepowered.api.plugin.PluginManager

inline val Server: Server get() = Sponge.getServer()

inline val PluginManager: PluginManager get() = Sponge.getPluginManager()
inline val CommandManager: CommandManager get() = Sponge.getCommandManager()
inline val EventManager: EventManager get() = Sponge.getEventManager()