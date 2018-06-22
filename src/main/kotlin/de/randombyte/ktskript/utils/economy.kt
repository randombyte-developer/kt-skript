package de.randombyte.ktskript.utils

import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.service.economy.EconomyService
import org.spongepowered.api.service.economy.account.UniqueAccount

fun Player.economyAccount(): UniqueAccount = EconomyService::class.service.getOrCreateAccount(uniqueId)
        .orElseThrow { RuntimeException("Could not create an economy account for $name!") }