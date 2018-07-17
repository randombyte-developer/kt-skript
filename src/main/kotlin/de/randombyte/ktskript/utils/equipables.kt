package de.randombyte.ktskript.utils

import org.spongepowered.api.data.type.HandType
import org.spongepowered.api.data.type.HandTypes
import org.spongepowered.api.data.type.HandTypes.MAIN_HAND
import org.spongepowered.api.entity.Equipable
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes

fun Equipable.hasItemInHand(handType: HandType = MAIN_HAND) = getEquipped(handType.equipmentType).isPresent

@Deprecated("The Sponge native method takes priority over this extension function.")
fun Equipable.getItemInHand(handType: HandType = MAIN_HAND) = getEquipped(handType.equipmentType).orElseThrow {
    RuntimeException("Equipable '$bestName' doesn't have anything in the '${handType.name}' hand!")
}

fun Equipable.itemInHand(handType: HandType = MAIN_HAND) = getEquipped(handType.equipmentType).orElseThrow {
    RuntimeException("Equipable '$bestName' doesn't have anything in the '${handType.name}' hand!")
}

private val HandType.equipmentType get() = when(this) {
    HandTypes.MAIN_HAND -> EquipmentTypes.MAIN_HAND
    HandTypes.OFF_HAND -> EquipmentTypes.OFF_HAND
    else -> throw RuntimeException("Illegal HandType!")
}