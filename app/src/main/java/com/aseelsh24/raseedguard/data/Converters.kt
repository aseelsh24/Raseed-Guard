package com.aseelsh24.raseedguard.data

import androidx.room.TypeConverter
import com.aseelsh24.raseedguard.core.PlanType
import com.aseelsh24.raseedguard.core.Unit
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Converters {
    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun fromPlanType(value: String): PlanType {
        return PlanType.valueOf(value)
    }

    @TypeConverter
    fun planTypeToString(type: PlanType): String {
        return type.name
    }

    @TypeConverter
    fun fromUnit(value: String): Unit {
        return Unit.valueOf(value)
    }

    @TypeConverter
    fun unitToString(unit: Unit): String {
        return unit.name
    }
}
