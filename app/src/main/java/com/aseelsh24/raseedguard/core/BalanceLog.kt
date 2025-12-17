package com.aseelsh24.raseedguard.core

import java.time.LocalDateTime

data class BalanceLog(
    val planId: String,
    val loggedAt: LocalDateTime,
    val remainingAmount: Double
)
