package de.randombyte.ktskript.utils.serializers

import com.google.common.reflect.TypeToken
import de.randombyte.ktskript.utils.Dates
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.objectmapping.ObjectMappingException
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object SimpleDateTypeSerializer : TypeSerializer<Date> {
    override fun deserialize(type: TypeToken<*>, value: ConfigurationNode): Date = Dates.deserialize(value.string)

    override fun serialize(type: TypeToken<*>, date: Date, value: ConfigurationNode) {
        value.value = Dates.serialize(date)
    }
}