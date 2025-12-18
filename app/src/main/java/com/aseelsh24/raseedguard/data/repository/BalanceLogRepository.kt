package com.aseelsh24.raseedguard.data.repository

import com.aseelsh24.raseedguard.core.BalanceLog
import kotlinx.coroutines.flow.Flow

interface BalanceLogRepository {
    fun getLogsForPlan(planId: String): Flow<List<BalanceLog>>
    suspend fun insertLog(log: BalanceLog)
}
