package de.randombyte.ktskript.utils

import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.item.inventory.Carrier
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.entity.MainPlayerInventory
import org.spongepowered.api.item.inventory.query.QueryOperationTypes
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult.Type.SUCCESS

/**
 * Tries to offer the given [itemStack] to this [Carrier]. If the carrier is a [Player] the items is
 * only tried to be offered to the [MainPlayerInventory].
 *
 * @return true if the transaction was successful
 */
fun Carrier.give(itemStack: ItemStack): Boolean {
    val inventory = when(this) {
        is Player -> inventory.query<Inventory>(QueryOperationTypes.INVENTORY_TYPE.of(MainPlayerInventory::class.java))
        else -> inventory
    }

    return inventory.offer(itemStack).type == SUCCESS
}