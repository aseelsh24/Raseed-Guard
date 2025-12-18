package com.aseelsh24.raseedguard.data.repository

import com.aseelsh24.raseedguard.core.BalanceLog
import com.aseelsh24.raseedguard.data.local.BalanceLogDao
import com.aseelsh24.raseedguard.data.local.BalanceLogEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BalanceLogRepositoryImpl(private val balanceLogDao: BalanceLogDao) : BalanceLogRepository {
    override fun getLogsForPlan(planId: String): Flow<List<BalanceLog>> {
        return balanceLogDao.getLogsForPlan(planId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertLog(log: BalanceLog) {
        balanceLogDao.insertLog(log.toEntity())
    }

    private fun BalanceLogEntity.toDomain(): BalanceLog {
        return BalanceLog(
            id = id,
            planId = planId,
            amount = amount,
            timestamp = timestamp
        )
    }

    private fun BalanceLog.toEntity(): BalanceLogEntity {
        return BalanceLogEntity(
            id = id,
            planId = planId,
            amount = amount,
            timestamp = timestamp
        )
    }
}
