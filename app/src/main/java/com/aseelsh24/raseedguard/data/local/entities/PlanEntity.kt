package com.aseelsh24.raseedguard.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aseelsh24.raseedguard.core.PlanType
import com.aseelsh24.raseedguard.core.Unit
import java.time.LocalDateTime

@Entity(tableName = "plans")
data class PlanEntity(
    @PrimaryKey val id: String,
    val type: PlanType,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val initialAmount: Double,
    val unit: Unit
)
