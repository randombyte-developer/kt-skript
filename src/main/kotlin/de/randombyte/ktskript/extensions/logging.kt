package de.randombyte.ktskript.extensions

fun log(message: String) = KtSkript.logger.info("LOG: $message")

fun warn(message: String) = KtSkript.logger.warn("WARNING: $message")

fun error(message: String) = KtSkript.logger.error("ERROR: $message")