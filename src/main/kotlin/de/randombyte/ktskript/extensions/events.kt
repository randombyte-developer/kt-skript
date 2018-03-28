package de.randombyte.ktskript.extensions

import de.randombyte.ktskript.KtSkript
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.Event
import org.spongepowered.api.event.block.InteractBlockEvent

fun <T : Event> registerEvent(eventClass: Class<T>, executor: (T) -> Unit) {
    val pluginInstance = Sponge.getPluginManager().getPlugin(KtSkript.ID).get().instance.get()
    EventManager.registerListener(pluginInstance, eventClass, { executor.invoke(it) })
}

inline fun <reified T : Event> registerEvent(crossinline executor: T.() -> Unit) = registerEvent(T::class.java) { executor(it) }


fun rightClickBlock(executor: InteractBlockEvent.Secondary.MainHand.() -> Unit) = registerEvent<InteractBlockEvent.Secondary.MainHand> { executor() }