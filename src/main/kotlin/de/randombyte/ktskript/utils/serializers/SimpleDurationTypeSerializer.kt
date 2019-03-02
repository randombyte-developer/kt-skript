package de.randombyte.ktskript.utils.serializers

import com.google.common.reflect.TypeToken
import de.randombyte.ktskript.utils.Durations
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer
import java.time.Duration

/**
 * See [Durations] for how the duration is formatted.
 */
object SimpleDurationTypeSerializer : TypeSerializer<Duration> {
    override fun deserialize(type: TypeToken<*>, node: ConfigurationNode) = Durations.deserialize(node.string)

    override fun serialize(type: TypeToken<*>, duration: Duration, node: ConfigurationNode) {
        node.value = Durations.serialize(duration)
    }
}