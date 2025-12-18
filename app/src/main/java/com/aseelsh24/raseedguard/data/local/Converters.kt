package com.aseelsh24.raseedguard.data.local

import androidx.room.TypeConverter
import com.aseelsh24.raseedguard.core.PlanType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.aseelsh24.raseedguard.core.Unit as PlanUnit

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
    fun fromPlanUnit(value: String): PlanUnit {
        return PlanUnit.valueOf(value)
    }

    @TypeConverter
    fun planUnitToString(unit: PlanUnit): String {
        return unit.name
    }
}
