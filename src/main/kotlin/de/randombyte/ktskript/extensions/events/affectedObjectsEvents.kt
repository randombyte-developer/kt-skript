package de.randombyte.ktskript.extensions.events

import org.spongepowered.api.event.block.ChangeBlockEvent
import org.spongepowered.api.event.entity.AffectEntityEvent

val ChangeBlockEvent.blockChanges
    get() = transactions

val AffectEntityEvent.affectedEntities
    get() = entities