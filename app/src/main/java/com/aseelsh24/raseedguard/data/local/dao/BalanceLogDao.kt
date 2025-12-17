package com.aseelsh24.raseedguard.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aseelsh24.raseedguard.data.local.entities.BalanceLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BalanceLogDao {
    @Query("SELECT * FROM balance_logs WHERE planId = :planId ORDER BY loggedAt DESC")
    fun getLogsForPlan(planId: String): Flow<List<BalanceLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: BalanceLogEntity)
}
