package de.randombyte.ktskript.extensions.events

import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Cancellable
import org.spongepowered.api.event.Event

val Event.causedByPlayer: Boolean
    get() = cause.containsType(Player::class.java)

val Event.causingPlayer: Player
    get() = cause.first(Player::class.java).orElseThrow { RuntimeException("No causing player available!") }

fun Cancellable.cancelEvent() = setCancelled(true)