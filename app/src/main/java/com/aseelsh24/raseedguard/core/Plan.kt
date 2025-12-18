package com.aseelsh24.raseedguard.core

import java.time.LocalDateTime
import com.aseelsh24.raseedguard.core.Unit as PlanUnit

data class Plan(
    val id: String,
    val type: PlanType,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val initialAmount: Double,
    val unit: PlanUnit
)
