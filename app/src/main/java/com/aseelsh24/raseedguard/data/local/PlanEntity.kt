package com.aseelsh24.raseedguard.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aseelsh24.raseedguard.core.PlanType
import java.time.LocalDateTime
import com.aseelsh24.raseedguard.core.Unit as PlanUnit

@Entity(tableName = "plans")
data class PlanEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val type: PlanType,
    val totalAmount: Double,
    val unit: PlanUnit,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime
)
