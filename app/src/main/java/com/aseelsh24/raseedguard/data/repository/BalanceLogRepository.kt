package com.aseelsh24.raseedguard.data.repository

import com.aseelsh24.raseedguard.core.BalanceLog
import com.aseelsh24.raseedguard.data.local.dao.BalanceLogDao
import com.aseelsh24.raseedguard.data.local.entities.BalanceLogEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface BalanceLogRepository {
    fun getLogsForPlan(planId: String): Flow<List<BalanceLog>>
    suspend fun insertLog(log: BalanceLog)
}

class BalanceLogRepositoryImpl(private val balanceLogDao: BalanceLogDao) : BalanceLogRepository {
    override fun getLogsForPlan(planId: String): Flow<List<BalanceLog>> {
        return balanceLogDao.getLogsForPlan(planId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertLog(log: BalanceLog) {
        balanceLogDao.insertLog(log.toEntity())
    }
}

private fun BalanceLogEntity.toDomain(): BalanceLog {
    return BalanceLog(
        planId = planId,
        loggedAt = loggedAt,
        remainingAmount = remainingAmount
    )
}

private fun BalanceLog.toEntity(): BalanceLogEntity {
    return BalanceLogEntity(
        planId = planId,
        loggedAt = loggedAt,
        remainingAmount = remainingAmount
    )
}
