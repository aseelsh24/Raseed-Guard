package com.aseelsh24.raseedguard

import android.content.Context
import androidx.room.Room
import com.aseelsh24.raseedguard.data.AppDatabase
import com.aseelsh24.raseedguard.data.repository.BalanceLogRepository
import com.aseelsh24.raseedguard.data.repository.BalanceLogRepositoryImpl
import com.aseelsh24.raseedguard.data.repository.PlanRepository
import com.aseelsh24.raseedguard.data.repository.PlanRepositoryImpl

interface AppContainer {
    val planRepository: PlanRepository
    val balanceLogRepository: BalanceLogRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    private val database: AppDatabase by lazy {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "raseed_guard_db"
        ).build()
    }

    override val planRepository: PlanRepository by lazy {
        PlanRepositoryImpl(database.planDao())
    }

    override val balanceLogRepository: BalanceLogRepository by lazy {
        BalanceLogRepositoryImpl(database.balanceLogDao())
    }
}
