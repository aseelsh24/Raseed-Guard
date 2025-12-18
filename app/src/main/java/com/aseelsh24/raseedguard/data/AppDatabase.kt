package com.aseelsh24.raseedguard.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aseelsh24.raseedguard.data.converters.Converters
import com.aseelsh24.raseedguard.data.dao.BalanceLogDao
import com.aseelsh24.raseedguard.data.dao.PlanDao
import com.aseelsh24.raseedguard.data.entity.BalanceLogEntity
import com.aseelsh24.raseedguard.data.entity.PlanEntity

@Database(entities = [PlanEntity::class, BalanceLogEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun planDao(): PlanDao
    abstract fun balanceLogDao(): BalanceLogDao
}
