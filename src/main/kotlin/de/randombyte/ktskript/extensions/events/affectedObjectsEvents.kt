package de.randombyte.ktskript.extensions.events

import org.spongepowered.api.block.BlockSnapshot
import org.spongepowered.api.data.Transaction
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.event.block.ChangeBlockEvent
import org.spongepowered.api.event.entity.AffectEntityEvent

val ChangeBlockEvent.blockChanges: MutableList<Transaction<BlockSnapshot>>
    get() = transactions

val AffectEntityEvent.affectedEntities: MutableList<Entity>
    get() = entities