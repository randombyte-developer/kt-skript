package de.randombyte.ktskript.extensions

import org.apache.commons.lang3.RandomUtils

fun randomDouble(start: Double = 0.0, end: Double) = RandomUtils.nextDouble(start, end)

fun randomInt(start: Int = 0, end: Int) = RandomUtils.nextInt(start, end)

fun randomBoolean(chance: Double = 0.5) = randomDouble(start = 0.0, end = 1.0) < chance.coerceIn(0.1..1.0)

val <T> List<T>.randomElement
    get() = get(randomInt(start = 0, end = lastIndex))