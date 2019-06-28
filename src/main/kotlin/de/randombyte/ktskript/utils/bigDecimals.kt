package de.randombyte.ktskript.utils

import java.math.BigDecimal

operator fun BigDecimal.plus(other: Int): BigDecimal = this + other.toBigDecimal()
operator fun BigDecimal.plus(other: Double): BigDecimal = this + other.toBigDecimal()

operator fun BigDecimal.minus(other: Int): BigDecimal = this - other.toBigDecimal()
operator fun BigDecimal.minus(other: Double): BigDecimal = this - other.toBigDecimal()

operator fun BigDecimal.times(other: Int): BigDecimal = this * other.toBigDecimal()
operator fun BigDecimal.times(other: Double): BigDecimal = this * other.toBigDecimal()

operator fun BigDecimal.div(other: Int): BigDecimal = this * other.toBigDecimal()
operator fun BigDecimal.div(other: Double): BigDecimal = this * other.toBigDecimal()