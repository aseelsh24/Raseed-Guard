package com.aseelsh24.raseedguard.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aseelsh24.raseedguard.data.Converters
import com.aseelsh24.raseedguard.data.local.dao.BalanceLogDao
import com.aseelsh24.raseedguard.data.local.dao.PlanDao
import com.aseelsh24.raseedguard.data.local.entities.BalanceLogEntity
import com.aseelsh24.raseedguard.data.local.entities.PlanEntity

@Database(entities = [PlanEntity::class, BalanceLogEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun planDao(): PlanDao
    abstract fun balanceLogDao(): BalanceLogDao
}
