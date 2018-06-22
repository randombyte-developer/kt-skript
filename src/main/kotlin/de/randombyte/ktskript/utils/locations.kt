package de.randombyte.ktskript.utils

import org.spongepowered.api.world.Locatable
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import org.spongepowered.api.world.extent.Extent

val Location<out Extent>.world: World
    get() = extent as? World ?: throw RuntimeException("The Location isn't in a World!")

fun <T : Locatable> Iterable<T>.getNearest(location: Location<World>): T? = filter {
    it.location.extent.uniqueId == location.extent.uniqueId
}.minBy {
    it.location.position.distance(location.position)
}