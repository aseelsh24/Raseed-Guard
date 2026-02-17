package com.aseelsh24.raseedguard.di

import android.content.Context
import androidx.room.Room
import com.aseelsh24.raseedguard.data.AppDatabase
import com.aseelsh24.raseedguard.data.dao.BalanceLogDao
import com.aseelsh24.raseedguard.data.dao.PlanDao
import com.aseelsh24.raseedguard.data.repository.BalanceLogRepository
import com.aseelsh24.raseedguard.data.repository.BalanceLogRepositoryImpl
import com.aseelsh24.raseedguard.data.repository.PlanRepository
import com.aseelsh24.raseedguard.data.repository.PlanRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "raseed_guard_database"
        ).build()
    }

    @Provides
    @Singleton
    fun providePlanDao(database: AppDatabase): PlanDao {
        return database.planDao()
    }

    @Provides
    @Singleton
    fun provideBalanceLogDao(database: AppDatabase): BalanceLogDao {
        return database.balanceLogDao()
    }

    @Provides
    @Singleton
    fun providePlanRepository(planDao: PlanDao): PlanRepository {
        return PlanRepositoryImpl(planDao)
    }

    @Provides
    @Singleton
    fun provideBalanceLogRepository(balanceLogDao: BalanceLogDao): BalanceLogRepository {
        return BalanceLogRepositoryImpl(balanceLogDao)
    }
}
