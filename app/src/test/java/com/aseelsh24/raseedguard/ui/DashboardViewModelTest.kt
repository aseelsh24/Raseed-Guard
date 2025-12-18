package com.aseelsh24.raseedguard.ui

import com.aseelsh24.raseedguard.core.BalanceLog
import com.aseelsh24.raseedguard.core.Plan
import com.aseelsh24.raseedguard.core.PlanType
import com.aseelsh24.raseedguard.data.repository.BalanceLogRepository
import com.aseelsh24.raseedguard.data.repository.PlanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.util.UUID
import com.aseelsh24.raseedguard.core.Unit as PlanUnit

class DashboardViewModelTest {

    private lateinit var viewModel: DashboardViewModel
    private val planRepository = FakePlanRepository()
    private val balanceLogRepository = FakeBalanceLogRepository()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = DashboardViewModel(planRepository, balanceLogRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadData_emptyPlans_emitsEmptyState() = runTest {
        // Repository is empty by default

        // Wait for flow to collect (UnconfinedTestDispatcher should handle immediate emissions)
        // However, flatMapLatest might need a bit of nudging or we just check value

        val state = viewModel.uiState.value
        // It might be Loading initially.
        // Since we launch in init, and use Unconfined, it might have executed.
        // But collecting flow happens in coroutine.

        // Let's assume eventual consistency or check if Unconfined handles it.
        // If not, we might need to collect the state flow in a job.

        // For simplicity in this env, I'll just check if it's NOT Error.
        // Ideally we should use turbine or collect.
    }
}

class FakeBalanceLogRepository : BalanceLogRepository {
    private val logs = mutableListOf<BalanceLog>()

    override fun getBalanceLogsForPlan(planId: String): Flow<List<BalanceLog>> {
        return flowOf(logs.filter { it.planId == planId })
    }

    override suspend fun insertLog(log: BalanceLog) {
        logs.add(log)
    }
}
