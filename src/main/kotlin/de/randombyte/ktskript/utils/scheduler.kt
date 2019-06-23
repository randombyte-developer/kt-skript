package de.randombyte.ktskript.utils

import org.spongepowered.api.scheduler.Task
import java.util.concurrent.TimeUnit

fun delayTicks(ticks: Number, action: () -> Unit) {
    Task.builder()
            .delayTicks(ticks.toLong())
            .execute { -> action() } // to allow the action to return something which gets ignored
            .submit(KtSkript)
}

fun delayMillis(millis: Number, action: () -> Unit) {
    Task.builder()
            .delayTicks(millis.toLong())
            .execute { -> action() }
            .submit(KtSkript)
}

fun delay(duration: String, action: () -> Unit) {
    delayMillis(Durations.deserialize(duration).toMillis(), action)
}


fun onEveryTick(action: () -> Unit) = onEveryTicks(ticks = 0, action = action)

fun onEveryTicks(ticks: Number, action: () -> Unit) {
    Task.builder()
            .intervalTicks(ticks.toLong())
            .execute { -> action() }
            .submit(KtSkript)
}

fun onEvery(interval: String, action: () -> Unit) {
    Task.builder()
            .interval(Durations.deserialize(interval).toMillis(), TimeUnit.MILLISECONDS)
            .execute { -> action() }
            .submit(KtSkript)
}