package com.aseelsh24.raseedguard.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
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
    @PrimaryKey
    val id: String,
    val planId: String,
    val amount: Double,
    val timestamp: LocalDateTime
)
