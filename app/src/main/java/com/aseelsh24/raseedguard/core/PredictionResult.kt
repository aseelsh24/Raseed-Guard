package com.aseelsh24.raseedguard.core

import java.time.LocalDateTime

enum class RiskLevel {
    SAFE, WARNING, CRITICAL
}

data class PredictionResult(
    val remainingNormalized: Double,
    val daysUntilEnd: Long,
    val dailyRate: Double,
    val smoothedDailyRate: Double,
    val predictedDepletionAt: LocalDateTime?,
    val riskLevel: RiskLevel,
    val safeDailyUsageTarget: Double
)
