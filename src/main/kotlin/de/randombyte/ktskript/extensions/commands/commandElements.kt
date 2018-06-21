package de.randombyte.ktskript.extensions.commands

import de.randombyte.ktskript.extensions.t
import org.spongepowered.api.command.args.GenericArguments.*

fun string(key: String) = string(key.t)

fun remainingStrings(key: String) = remainingRawJoinedStrings(key.t)

fun integer(key: String) = integer(key.t)

fun double(key: String) = doubleNum(key.t)

fun choice(key: String, vararg choices: String) = choices(key.t, choices.associateBy { it })

fun choice(key: String, choices: Map<String, Any>) = choices(key.t, choices)

fun player(key: String, mustBeOnline: Boolean = true) = if (mustBeOnline) player(key.t) else user(key.t)