package de.randombyte.ktskript.extensions

import org.spongepowered.api.text.Text

fun broadcast(text: Text) = Server.broadcastChannel.send(text)