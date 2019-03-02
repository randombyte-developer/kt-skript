package de.randombyte.ktskript.utils

import ninja.leaping.configurate.objectmapping.ObjectMappingException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Dates {
    private val dateFormat = SimpleDateFormat("HH:mm:ss.SSS-dd.MM.yyyy")

    fun deserialize(string: String): Date {
        try {
            return dateFormat.parse(string)
        } catch (exception: ParseException) {
            throw ObjectMappingException("Invalid input value '$string' for a date like this: '21:18:25.300-28.03.2017'", exception)
        }
    }

    fun serialize(date: Date): String = dateFormat.format(date)
}
