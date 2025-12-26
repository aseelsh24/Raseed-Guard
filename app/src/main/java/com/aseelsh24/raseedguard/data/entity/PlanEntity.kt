package com.aseelsh24.raseedguard.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aseelsh24.raseedguard.core.Plan
import com.aseelsh24.raseedguard.core.PlanCategory
import com.aseelsh24.raseedguard.core.PlanType
import java.time.LocalDateTime
import com.aseelsh24.raseedguard.core.PlanUnit

@Entity(tableName = "plans")
data class PlanEntity(
    @PrimaryKey
    val id: String,
    val type: PlanType,
    val category: PlanCategory = if (type == PlanType.VOICE) PlanCategory.VOICE else PlanCategory.MOBILE,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val initialAmount: Double,
    val unit: PlanUnit
)

fun PlanEntity.toDomain() = Plan(
    id = id,
    type = type,
    category = category,
    startAt = startAt,
    endAt = endAt,
    initialAmount = initialAmount,
    unit = unit
)

fun Plan.toEntity() = PlanEntity(
    id = id,
    type = type,
    category = category,
    startAt = startAt,
    endAt = endAt,
    initialAmount = initialAmount,
    unit = unit
)
