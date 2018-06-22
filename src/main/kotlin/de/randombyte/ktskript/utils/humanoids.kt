package de.randombyte.ktskript.utils

import org.spongepowered.api.command.CommandException
import org.spongepowered.api.data.type.HandTypes
import org.spongepowered.api.entity.EntityTypes
import org.spongepowered.api.entity.living.Humanoid
import org.spongepowered.api.item.inventory.Carrier
import org.spongepowered.api.item.inventory.ItemStack

/**
 * Gives the [Humanoid] the [itemStack] by trying these things:
 * 1. putting it in the hand
 * 2. putting it somewhere in the inventory
 * 3. dropping it onto the ground
 */
fun Humanoid.give(itemStack: ItemStack) { // todo more general: Any.give()?
    fun ItemStack.singleCopy(): ItemStack = copy().apply { quantity = 1 }

    val itemInHand = getItemInHand(HandTypes.MAIN_HAND).orNull()

    if (itemInHand == null) {
        // nothing in hand -> put item in hand
        setItemInHand(HandTypes.MAIN_HAND, itemStack)
    } else {
        if (itemInHand.singleCopy().equalTo(itemStack.singleCopy())) {
            itemInHand.quantity += itemStack.quantity
            setItemInHand(HandTypes.MAIN_HAND, itemInHand)
            return
        }

        // something in hand -> place item somewhere in inventory
        val success = (this as Carrier).give(itemStack)
        if (!success) {
            // inventory full -> spawn as item
            val itemEntity = location.extent.createEntity(EntityTypes.ITEM, location.position)
            itemEntity.representedItem = itemStack.createSnapshot()
            if (!location.extent.spawnEntity(itemEntity)) {
                throw CommandException("Couldn't spawn Item!".t)
            }
        }
    }
}