package de.randombyte.ktskript.utils.events

import de.randombyte.ktskript.utils.EventManager
import de.randombyte.ktskript.utils.KtSkript
import org.spongepowered.api.entity.EntityTypes
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Event
import org.spongepowered.api.event.block.ChangeBlockEvent
import org.spongepowered.api.event.block.InteractBlockEvent
import org.spongepowered.api.event.entity.*
import org.spongepowered.api.event.network.ClientConnectionEvent

fun <T : Event> registerEvent(eventClass: Class<T>, executor: (T) -> Unit) {
    EventManager.registerListener(KtSkript, eventClass, executor::invoke)
}

inline fun <reified T : Event> registerEvent(noinline executor: T.() -> Unit) = registerEvent(T::class.java, executor)

// === EVENTS ===

// INTERACTION

fun onBlockLeftClick(executor: InteractBlockEvent.Primary.MainHand.() -> Unit) = registerEvent(executor)

fun onBlockRightClick(executor: InteractBlockEvent.Secondary.MainHand.() -> Unit) = registerEvent(executor)

fun onEntityLeftClick(executor: InteractEntityEvent.Primary.MainHand.() -> Unit) = registerEvent(executor)

fun onEntityRightClick(executor: InteractEntityEvent.Secondary.MainHand.() -> Unit) = registerEvent(executor)

// MOVE

fun onEntityMove(executor: MoveEntityEvent.() -> Unit) = registerEvent(executor)

fun onPlayerMove(executor: MoveEntityEvent.() -> Unit) = onEntityMove { if (targetEntity.type == EntityTypes.PLAYER) executor() }

// BLOCKS

fun onBlockBreak(executor: ChangeBlockEvent.Break.() -> Unit) = registerEvent(executor)

fun onBlockPlace(executor: ChangeBlockEvent.Place.() -> Unit) = registerEvent(executor)

// ENTITIES

fun onEntitySpawn(executor: SpawnEntityEvent.ChunkLoad.() -> Unit) = registerEvent(executor)

fun onEntityDamage(executor: DamageEntityEvent.() -> Unit) = registerEvent(executor)

fun onEntityRemove(executor: DestructEntityEvent.() -> Unit) = registerEvent(executor)

fun onEntityDeath(executor: DestructEntityEvent.Death.() -> Unit) = registerEvent(executor)

// PLAYERS

fun onPlayerLogin(executor: ClientConnectionEvent.Login.() -> Unit) = registerEvent(executor)

fun onPlayerJoin(executor: ClientConnectionEvent.Join.() -> Unit) = registerEvent(executor)

fun onPlayerLeave(executor: ClientConnectionEvent.Disconnect.() -> Unit) = registerEvent(executor)

fun onPlayerDamage(executor: DamageEntityEvent.() -> Unit) = onEntityDamage { if (targetEntity is Player) executor() }

fun onPlayerDeath(executor: DestructEntityEvent.Death.() -> Unit) = onEntityDeath { if (targetEntity is Player) executor() }