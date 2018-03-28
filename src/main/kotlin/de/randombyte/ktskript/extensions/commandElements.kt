package de.randombyte.ktskript.extensions

import de.randombyte.kosp.extensions.toText
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.args.GenericArguments.*

fun word(key: String) = string(key.toText())

fun text(key: String) = remainingRawJoinedStrings(key.toText())

fun integer(key: String) = integer(key.toText())

fun double(key: String) = GenericArguments.doubleNum(key.toText())

fun choice(key: String, vararg choices: String) = choices(key.toText(), choices.associateBy { it })

fun player(key: String, online: Boolean = true) = if (online) player(key.toText()) else user(key.toText())