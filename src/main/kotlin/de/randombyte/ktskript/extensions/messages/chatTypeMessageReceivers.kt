package de.randombyte.ktskript.extensions.messages

import org.spongepowered.api.text.channel.ChatTypeMessageReceiver
import org.spongepowered.api.text.chat.ChatType
import org.spongepowered.api.text.serializer.TextSerializers

fun ChatTypeMessageReceiver.sendMessage(chatType: ChatType, text: String) {
    sendMessage(chatType, TextSerializers.FORMATTING_CODE.deserialize(text))
}