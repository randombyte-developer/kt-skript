package de.randombyte.ktskript.extensions

import org.spongepowered.api.item.inventory.Carrier
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult.Type.SUCCESS

fun Carrier.give(itemStack: ItemStack) = inventory.offer(itemStack).type == SUCCESS