package de.randombyte.ktskript.utils.serializers

import com.google.common.reflect.TypeToken
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.objectmapping.ObjectMappingException
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object SimpleDateTypeSerializer : TypeSerializer<Date> {

    private val dateFormat = SimpleDateFormat("HH:mm:ss.SSS-dd.MM.yyyy")

    override fun deserialize(type: TypeToken<*>, value: ConfigurationNode): Date = deserialize(value.string)

    override fun serialize(type: TypeToken<*>, date: Date, value: ConfigurationNode) {
        value.value = serialize(date)
    }

    fun deserialize(string: String): Date {
        try {
            return dateFormat.parse(string)
        } catch (exception: ParseException) {
            throw ObjectMappingException("Invalid input value '$string' for a date like this: '21:18:25.300-28.03.2017'", exception)
        }
    }

    fun serialize(date: Date): String = dateFormat.format(date)
}