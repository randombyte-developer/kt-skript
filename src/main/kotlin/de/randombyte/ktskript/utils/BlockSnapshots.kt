package de.randombyte.ktskript.utils

import org.spongepowered.api.block.BlockSnapshot
import org.spongepowered.api.block.BlockType
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.extent.Extent

val BlockSnapshot.type: BlockType
    get() = state.type

val BlockSnapshot.blockLocation: Location<out Extent>
    get() = location.orElseThrow { RuntimeException("This BlockSnapshot doesn't have a Location!") }