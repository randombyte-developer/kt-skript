package de.randombyte.ktskript.extensions

import org.spongepowered.api.text.serializer.TextSerializers

val String.t
    get() = TextSerializers.FORMATTING_CODE.deserialize(this)