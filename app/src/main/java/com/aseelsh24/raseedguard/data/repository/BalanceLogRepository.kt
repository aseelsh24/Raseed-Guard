package com.aseelsh24.raseedguard.data.repository

import com.aseelsh24.raseedguard.core.BalanceLog
import com.aseelsh24.raseedguard.data.dao.BalanceLogDao
import com.aseelsh24.raseedguard.data.entity.toDomain
import com.aseelsh24.raseedguard.data.entity.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface BalanceLogRepository {
    fun getBalanceLogsForPlan(planId: String): Flow<List<BalanceLog>>
    suspend fun insertLog(log: BalanceLog)
}

class BalanceLogRepositoryImpl(private val balanceLogDao: BalanceLogDao) : BalanceLogRepository {
    override fun getBalanceLogsForPlan(planId: String): Flow<List<BalanceLog>> {
        return balanceLogDao.getBalanceLogsForPlan(planId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertLog(log: BalanceLog) {
        balanceLogDao.insertLog(log.toEntity())
    }
}
