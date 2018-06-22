package de.randombyte.ktskript.utils

import de.randombyte.ktskript.utils.serializers.SimpleDurationTypeSerializer
import org.spongepowered.api.scheduler.Task

fun delayTicks(ticks: Number, action: () -> Any) {
    Task.builder()
            .delayTicks(ticks.toLong())
            .execute { -> action() } // to allow the action to return something which gets ignored
            .submit(KtSkript)
}

fun delayMillis(millis: Number, action: () -> Any) {
    Task.builder()
            .delayTicks(millis.toLong())
            .execute { -> action() }
            .submit(KtSkript)
}

fun delay(duration: String, action: () -> Any) {
    delayMillis(SimpleDurationTypeSerializer.deserialize(duration).toMillis(), action)
}