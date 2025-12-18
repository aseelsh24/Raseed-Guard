package com.aseelsh24.raseedguard.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BalanceLogDao {
    @Query("SELECT * FROM balance_logs WHERE planId = :planId ORDER BY timestamp DESC")
    fun getLogsForPlan(planId: String): Flow<List<BalanceLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: BalanceLogEntity)
}
