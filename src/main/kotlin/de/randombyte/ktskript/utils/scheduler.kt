package de.randombyte.ktskript.utils

import org.spongepowered.api.scheduler.Task
import java.util.concurrent.TimeUnit

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
    delayMillis(Durations.deserialize(duration).toMillis(), action)
}


fun onEveryTick(action: () -> Any) = onEveryTicks(ticks = 0, action = action)

fun onEveryTicks(ticks: Number, action: () -> Any) {
    Task.builder()
            .intervalTicks(ticks.toLong())
            .execute { -> action() }
            .submit(KtSkript)
}

fun onEvery(interval: String, action: () -> Any) {
    Task.builder()
            .interval(Durations.deserialize(interval).toMillis(), TimeUnit.MILLISECONDS)
            .execute { -> action() }
            .submit(KtSkript)
}