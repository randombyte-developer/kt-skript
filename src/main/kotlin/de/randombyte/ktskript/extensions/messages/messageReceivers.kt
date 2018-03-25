package de.randombyte.ktskript.extensions.messages

import org.spongepowered.api.text.channel.MessageReceiver
import org.spongepowered.api.text.serializer.TextSerializers

fun MessageReceiver.sendMessage(text: String) {
    sendMessage(TextSerializers.FORMATTING_CODE.deserialize(text))
}