package de.randombyte.ktskript.utils.commands

import de.randombyte.ktskript.utils.t
import org.spongepowered.api.CatalogType
import org.spongepowered.api.command.args.GenericArguments.*

fun boolean(key: String) = bool(key.t)

fun string(key: String) = string(key.t)

fun remainingStrings(key: String) = remainingRawJoinedStrings(key.t)

fun integer(key: String) = integer(key.t)

fun double(key: String) = doubleNum(key.t)

fun choice(key: String, vararg choices: String) = choices(key.t, choices.associateBy { it })

fun choice(key: String, choices: Map<String, Any>) = choices(key.t, choices)

fun player(key: String) = player(key.t)

fun user(key: String) = user(key.t)

inline fun <reified T : CatalogType> catalogedElement(key: String) = catalogedElement(key.t, T::class.java)