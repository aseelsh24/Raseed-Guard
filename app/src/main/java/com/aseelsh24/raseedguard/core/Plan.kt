package com.aseelsh24.raseedguard.core

import java.time.LocalDateTime

data class Plan(
    val id: String,
    val type: PlanType,
    val category: PlanCategory,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val initialAmount: Double,
    val unit: PlanUnit
)
