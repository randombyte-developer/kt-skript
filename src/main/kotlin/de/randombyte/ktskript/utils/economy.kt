package de.randombyte.ktskript.utils

import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.service.economy.EconomyService
import org.spongepowered.api.service.economy.account.UniqueAccount

val User.economyAccount: UniqueAccount
    get() = EconomyService::class.service.getOrCreateAccount(uniqueId)
            .orElseThrow { RuntimeException("Could not create an economy account for $name!") }