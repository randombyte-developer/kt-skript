package de.randombyte.ktskript.utils

import org.spongepowered.api.Server
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandManager
import org.spongepowered.api.event.EventManager
import org.spongepowered.api.plugin.PluginManager
import org.spongepowered.api.scheduler.Scheduler
import org.spongepowered.api.service.ServiceManager
import org.spongepowered.api.service.economy.EconomyService as SpongeEconomyService

inline val Server: Server get() = Sponge.getServer()

inline val PluginManager: PluginManager get() = Sponge.getPluginManager()
inline val CommandManager: CommandManager get() = Sponge.getCommandManager()
inline val EventManager: EventManager get() = Sponge.getEventManager()
inline val ServiceManager: ServiceManager get() = Sponge.getServiceManager()
inline val Scheduler: Scheduler get() = Sponge.getScheduler()

inline val EconomyService: SpongeEconomyService get() = SpongeEconomyService::class.service