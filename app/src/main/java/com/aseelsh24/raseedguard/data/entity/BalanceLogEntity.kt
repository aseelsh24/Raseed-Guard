package com.aseelsh24.raseedguard.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.aseelsh24.raseedguard.core.BalanceLog
import java.time.LocalDateTime

@Entity(
    tableName = "balance_logs",
    foreignKeys = [
        ForeignKey(
            entity = PlanEntity::class,
            parentColumns = ["id"],
            childColumns = ["planId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BalanceLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val planId: String,
    val loggedAt: LocalDateTime,
    val remainingAmount: Double
)

fun BalanceLogEntity.toDomain() = BalanceLog(
    planId = planId,
    loggedAt = loggedAt,
    remainingAmount = remainingAmount
)

fun BalanceLog.toEntity() = BalanceLogEntity(
    planId = planId,
    loggedAt = loggedAt,
    remainingAmount = remainingAmount
)
