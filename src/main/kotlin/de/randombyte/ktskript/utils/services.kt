package de.randombyte.ktskript.utils

import kotlin.reflect.KClass

fun <T : Any> KClass<T>.isAvailable() = ServiceManager.provide(this.java).isPresent

val <T : Any> KClass<T>.service get() = ServiceManager.provide(this.java)
        .orElseThrow { RuntimeException("Could not load ${this.java.name}''!") }