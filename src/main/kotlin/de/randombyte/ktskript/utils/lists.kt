package de.randombyte.ktskript.utils

import java.util.*

fun <T> List<T>.getRandomElement(random: Random): T = get(random.nextInt(size))