package de.randombyte.ktskript.utils.events

import de.randombyte.ktskript.script.UnloadScriptsEvent
import de.randombyte.ktskript.utils.EventManager
import de.randombyte.ktskript.utils.KtSkript
import org.spongepowered.api.entity.EntityTypes
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Event
import org.spongepowered.api.event.block.ChangeBlockEvent
import org.spongepowered.api.event.block.InteractBlockEvent
import org.spongepowered.api.event.entity.*
import org.spongepowered.api.event.network.ClientConnectionEvent

fun <T : Event> registerListener(eventClass: Class<T>, executor: (T) -> Unit) {
    EventManager.registerListener(KtSkript, eventClass, executor::invoke)
}

inline fun <reified T : Event> registerListener(noinline executor: T.() -> Unit) = registerListener(T::class.java, executor)

// === EVENTS ===

// INTERACTION

fun onBlockLeftClick(executor: InteractBlockEvent.Primary.MainHand.() -> Unit) = registerListener(executor)

fun onBlockRightClick(executor: InteractBlockEvent.Secondary.MainHand.() -> Unit) = registerListener(executor)

fun onEntityLeftClick(executor: InteractEntityEvent.Primary.MainHand.() -> Unit) = registerListener(executor)

fun onEntityRightClick(executor: InteractEntityEvent.Secondary.MainHand.() -> Unit) = registerListener(executor)

// MOVE

fun onEntityMove(executor: MoveEntityEvent.() -> Unit) = registerListener(executor)

fun onPlayerMove(executor: MoveEntityEvent.() -> Unit) = onEntityMove { if (targetEntity.type == EntityTypes.PLAYER) executor() }

// BLOCKS

fun onBlockBreak(executor: ChangeBlockEvent.Break.() -> Unit) = registerListener(executor)

fun onBlockPlace(executor: ChangeBlockEvent.Place.() -> Unit) = registerListener(executor)

// ENTITIES

fun onEntitySpawn(executor: SpawnEntityEvent.() -> Unit) = registerListener(executor)

fun onEntityDamage(executor: DamageEntityEvent.() -> Unit) = registerListener(executor)

fun onEntityRemove(executor: DestructEntityEvent.() -> Unit) = registerListener(executor)

fun onEntityDeath(executor: DestructEntityEvent.Death.() -> Unit) = registerListener(executor)

// PLAYERS

fun onPlayerLogin(executor: ClientConnectionEvent.Login.() -> Unit) = registerListener(executor)

fun onPlayerJoin(executor: ClientConnectionEvent.Join.() -> Unit) = registerListener(executor)

fun onPlayerLeave(executor: ClientConnectionEvent.Disconnect.() -> Unit) = registerListener(executor)

fun onPlayerDamage(executor: DamageEntityEvent.() -> Unit) = onEntityDamage { if (targetEntity is Player) executor() }

fun onPlayerDeath(executor: DestructEntityEvent.Death.() -> Unit) = onEntityDeath { if (targetEntity is Player) executor() }

// OTHERS

fun onScriptsUnload(executor: UnloadScriptsEvent.() -> Unit) = registerListener(executor)