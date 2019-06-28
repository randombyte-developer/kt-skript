package de.randombyte.ktskript.utils

import java.util.*

/**
 * Converts the Java [Optional] into the Kotlin equivalent.
 */
fun <T> Optional<T>.orNull(): T? = orElse(null)

/**
 * And the other way around.
 */
fun <T> T?.toOptional(): Optional<T> = Optional.ofNullable(this)