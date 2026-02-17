package com.aseelsh24.raseedguard.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aseelsh24.raseedguard.core.BalanceLog
import com.aseelsh24.raseedguard.core.Plan
import com.aseelsh24.raseedguard.core.PlanType
import com.aseelsh24.raseedguard.core.Unit
import com.aseelsh24.raseedguard.data.local.AppDatabase
import com.aseelsh24.raseedguard.data.local.BalanceLogDao
import com.aseelsh24.raseedguard.data.local.PlanDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private lateinit var database: AppDatabase
    private lateinit var planDao: PlanDao
    private lateinit var balanceLogDao: BalanceLogDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        planDao = database.planDao()
        balanceLogDao = database.balanceLogDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndRetrievePlan() = runBlocking {
        // Given
        val plan = Plan(
            id = "test-plan",
            type = PlanType.INTERNET,
            startAt = LocalDateTime.now(),
            endAt = LocalDateTime.now().plusDays(30),
            initialAmount = 10.0,
            unit = Unit.GB
        )

        // When
        planDao.insert(plan)
        val plans = planDao.getAllPlans().first()

        // Then
        assert(plans.size == 1)
        assert(plans[0].id == "test-plan")
    }

    @Test
    fun insertAndRetrieveBalanceLog() = runBlocking {
        // Given
        val plan = Plan(
            id = "test-plan",
            type = PlanType.INTERNET,
            startAt = LocalDateTime.now(),
            endAt = LocalDateTime.now().plusDays(30),
            initialAmount = 10.0,
            unit = Unit.GB
        )
        planDao.insert(plan)

        val log = BalanceLog(
            planId = "test-plan",
            loggedAt = LocalDateTime.now(),
            remainingAmount = 8.0
        )

        // When
        balanceLogDao.insert(log)
        val logs = balanceLogDao.getLogsForPlan("test-plan").first()

        // Then
        assert(logs.size == 1)
        assert(logs[0].remainingAmount == 8.0)
    }

    @Test
    fun deleteOldLogs() = runBlocking {
        // Given
        val planId = "test-plan"
        val oldLog = BalanceLog(
            planId = planId,
            loggedAt = LocalDateTime.now().minusDays(100),
            remainingAmount = 5.0
        )
        val recentLog = BalanceLog(
            planId = planId,
            loggedAt = LocalDateTime.now(),
            remainingAmount = 8.0
        )

        balanceLogDao.insert(oldLog)
        balanceLogDao.insert(recentLog)

        // When
        balanceLogDao.deleteOldLogs(LocalDateTime.now().minusDays(90))
        val logs = balanceLogDao.getLogsForPlan(planId).first()

        // Then
        assert(logs.size == 1)
        assert(logs[0].loggedAt.isAfter(LocalDateTime.now().minusDays(90)))
    }
}
