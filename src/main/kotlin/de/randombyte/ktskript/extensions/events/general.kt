package de.randombyte.ktskript.extensions.events

import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Event

val Event.causingPlayer: Player
    get() = cause.first(Player::class.java).orElseThrow { RuntimeException("No causing player available!") }