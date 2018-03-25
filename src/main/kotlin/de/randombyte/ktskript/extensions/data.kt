package de.randombyte.ktskript.extensions

import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.manipulator.DataManipulator
import org.spongepowered.api.data.manipulator.mutable.RepresentedItemData
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData
import org.spongepowered.api.item.inventory.ItemStackSnapshot

private inline fun <reified T : DataManipulator<*, *>> DataHolder.getOrThrow() = getOrCreate(T::class.java).orElseThrow {
    IllegalArgumentException("'${this::class.java.simpleName}' doesn't support '${T::class.simpleName}'!")
}

var DataHolder.health: Double
    get() = getOrThrow<HealthData>().health().get()
    set(value) { getOrThrow<HealthData>().health().set(value) }

var DataHolder.representedItem: ItemStackSnapshot
    get() = getOrThrow<RepresentedItemData>().item().get()
    set(value) { getOrThrow<RepresentedItemData>().item().set(value) }