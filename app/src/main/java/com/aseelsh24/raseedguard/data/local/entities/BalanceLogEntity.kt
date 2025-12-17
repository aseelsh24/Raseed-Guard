package com.aseelsh24.raseedguard.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
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
    ],
    indices = [Index(value = ["planId"])]
)
data class BalanceLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val planId: String,
    val loggedAt: LocalDateTime,
    val remainingAmount: Double
)
