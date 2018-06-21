package de.randombyte.ktskript.extensions

import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import org.spongepowered.api.world.extent.Extent

val Location<out Extent>.world: World
    get() = extent as? World ?: throw RuntimeException("The Location isn't in a World!")