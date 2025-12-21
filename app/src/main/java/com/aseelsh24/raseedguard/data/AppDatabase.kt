package com.aseelsh24.raseedguard.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.aseelsh24.raseedguard.data.converters.Converters
import com.aseelsh24.raseedguard.data.dao.BalanceLogDao
import com.aseelsh24.raseedguard.data.dao.PlanDao
import com.aseelsh24.raseedguard.data.entity.BalanceLogEntity
import com.aseelsh24.raseedguard.data.entity.PlanEntity

@Database(entities = [PlanEntity::class, BalanceLogEntity::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun planDao(): PlanDao
    abstract fun balanceLogDao(): BalanceLogDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Add column with default 'MOBILE'
                db.execSQL("ALTER TABLE plans ADD COLUMN category TEXT NOT NULL DEFAULT 'MOBILE'")

                // 2. Fix Voice plans (assuming 'VOICE' is the string value for PlanType.VOICE)
                db.execSQL("UPDATE plans SET category = 'VOICE' WHERE type = 'VOICE'")
            }
        }
    }
}
