package de.randombyte.ktskript.utils

import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.manipulator.DataManipulator
import org.spongepowered.api.data.manipulator.mutable.RepresentedItemData
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData
import org.spongepowered.api.data.manipulator.mutable.entity.InvisibilityData
import org.spongepowered.api.data.manipulator.mutable.entity.SneakingData
import org.spongepowered.api.item.inventory.ItemStackSnapshot

private inline fun <reified T : DataManipulator<*, *>> DataHolder.getOrThrow() = getOrCreate(T::class.java).orElseThrow {
    IllegalArgumentException("'${this::class.java.simpleName}' doesn't support '${T::class.simpleName}'!")
}


var DataHolder.health: Double
    get() = getOrThrow<HealthData>().health().get()
    set(value) { getOrThrow<HealthData>().health().set(value) }

var DataHolder.invisible: Boolean
    get() = getOrThrow<InvisibilityData>().invisible().get()
    set(value) { getOrThrow<InvisibilityData>().invisible().set(value) }

var DataHolder.vanished: Boolean
    get() = getOrThrow<InvisibilityData>().vanish().get()
    set(value) { getOrThrow<InvisibilityData>().vanish().set(value) }

val DataHolder.isSneaking: Boolean
    get() = getOrThrow<SneakingData>().sneaking().get()


var DataHolder.representedItem: ItemStackSnapshot
    get() = getOrThrow<RepresentedItemData>().item().get()
    set(value) { getOrThrow<RepresentedItemData>().item().set(value) }