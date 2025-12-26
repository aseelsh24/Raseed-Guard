package com.aseelsh24.raseedguard.data.converters

import androidx.room.TypeConverter
import com.aseelsh24.raseedguard.core.PlanType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.aseelsh24.raseedguard.core.PlanUnit

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
    fun fromPlanType(value: String?): PlanType? {
        return value?.let { PlanType.valueOf(it) }
    }

    @TypeConverter
    fun planTypeToString(type: PlanType?): String? {
        return type?.name
    }

    @TypeConverter
    fun fromUnit(value: String?): PlanUnit? {
        return value?.let { PlanUnit.valueOf(it) }
    }

    @TypeConverter
    fun unitToString(unit: PlanUnit?): String? {
        return unit?.name
    }

    @TypeConverter
    fun fromPlanCategory(value: String?): com.aseelsh24.raseedguard.core.PlanCategory? {
        return value?.let { com.aseelsh24.raseedguard.core.PlanCategory.valueOf(it) }
    }

    @TypeConverter
    fun planCategoryToString(category: com.aseelsh24.raseedguard.core.PlanCategory?): String? {
        return category?.name
    }
}
