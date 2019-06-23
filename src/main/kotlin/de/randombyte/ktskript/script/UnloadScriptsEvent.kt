package de.randombyte.ktskript.script

import org.spongepowered.api.event.Event
import org.spongepowered.api.event.cause.Cause

/**
 * Fired when scripts are about to be unloaded. Useful for config saving.
 */
class UnloadScriptsEvent(val scripts: List<String>, private val _cause: Cause) : Event {
    override fun getCause() = _cause
}