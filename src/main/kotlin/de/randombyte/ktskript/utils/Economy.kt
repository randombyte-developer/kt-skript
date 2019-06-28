package de.randombyte.ktskript.utils

import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.service.economy.EconomyService
import org.spongepowered.api.service.economy.account.Account
import org.spongepowered.api.service.economy.account.UniqueAccount
import java.math.BigDecimal
import java.util.*

fun EconomyService.getAccount(identifier: String): Account = getOrCreateAccount(identifier)
        .orElseThrow { IllegalArgumentException("Could not get or create economy account '$identifier'!") }

fun EconomyService.getAccount(uuid: UUID): UniqueAccount = getOrCreateAccount(uuid)
        .orElseThrow { IllegalArgumentException("Could not get or create economy account '$uuid'!") }

fun EconomyService.getCurrencyById(id: String) = currencies.find { it.id == id }
        ?: throw IllegalArgumentException("Could not find currency '$id'!")

var Account.balance: BigDecimal
    get() = getBalance(EconomyService.defaultCurrency)
    set(balance) { setBalance(EconomyService.defaultCurrency, balance, KtSkript.cause) }

val User.economyAccount: UniqueAccount
    get() = EconomyService.getAccount(uniqueId)