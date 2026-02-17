package com.aseelsh24.raseedguard.utils

import com.aseelsh24.raseedguard.core.Unit

object FormatUtils {
    fun formatDataAmount(amount: Double, unit: Unit): String {
        return when (unit) {
            Unit.MB -> "%.2f MB".format(amount)
            Unit.GB -> "%.2f GB".format(amount)
            Unit.MINUTES -> "%.0f دقيقة".format(amount)
        }
    }

    fun formatPercentage(value: Double): String {
        return "%.1f%%".format(value)
    }

    fun formatDailyRate(rate: Double): String {
        return "%.2f يومياً".format(rate)
    }

    fun getRemainingDaysText(days: Long): String {
        return when {
            days < 0 -> "منتهية"
            days == 0L -> "ينتهي اليوم"
            days == 1L -> "يوم واحد متبقي"
            days == 2L -> "يومان متبقيان"
            days in 3..10 -> "$days أيام متبقية"
            else -> "$days يوماً متبقياً"
        }
    }
}
