package com.aseelsh24.raseedguard.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.aseelsh24.raseedguard.data.entity.BalanceLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BalanceLogDao {
    @Query("SELECT * FROM balance_logs WHERE planId = :planId ORDER BY loggedAt DESC")
    fun getBalanceLogsForPlan(planId: String): Flow<List<BalanceLogEntity>>

    @Insert
    suspend fun insertLog(log: BalanceLogEntity)
}
