package de.randombyte.ktskript.utils.events

import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.entity.TargetEntityEvent

val TargetEntityEvent.player: Player
    get() = (targetEntity as? Player) ?: throw RuntimeException("The 'targetEntity' is not a Player!")