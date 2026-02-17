package com.aseelsh24.raseedguard.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object DateUtils {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    fun formatDate(date: LocalDateTime): String {
        return date.format(dateFormatter)
    }

    fun formatDateTime(date: LocalDateTime): String {
        return date.format(dateTimeFormatter)
    }

    fun daysBetween(start: LocalDateTime, end: LocalDateTime): Long {
        return ChronoUnit.DAYS.between(start, end)
    }

    fun isToday(date: LocalDateTime): Boolean {
        val today = LocalDateTime.now()
        return date.year == today.year &&
                date.month == today.month &&
                date.dayOfMonth == today.dayOfMonth
    }

    fun isThisWeek(date: LocalDateTime): Boolean {
        val now = LocalDateTime.now()
        val weekStart = now.minusDays(now.dayOfWeek.value.toLong() - 1)
        val weekEnd = weekStart.plusDays(6)
        return date.isAfter(weekStart) && date.isBefore(weekEnd)
    }
}
