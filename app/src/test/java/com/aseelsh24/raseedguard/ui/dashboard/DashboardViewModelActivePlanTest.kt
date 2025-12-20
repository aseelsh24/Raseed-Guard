package com.aseelsh24.raseedguard.ui.dashboard

import com.aseelsh24.raseedguard.core.BalanceLog
import com.aseelsh24.raseedguard.core.Plan
import com.aseelsh24.raseedguard.core.PlanType
import com.aseelsh24.raseedguard.core.Unit as PlanUnit
import com.aseelsh24.raseedguard.data.repository.BalanceLogRepository
import com.aseelsh24.raseedguard.data.repository.PlanRepository
import com.aseelsh24.raseedguard.data.repository.SettingsRepository
import com.aseelsh24.raseedguard.ui.DashboardUiState
import com.aseelsh24.raseedguard.ui.DashboardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class DashboardViewModelActivePlanTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var planRepository: FakePlanRepository
    private lateinit var balanceLogRepository: FakeBalanceLogRepository
    private lateinit var settingsRepository: FakeSettingsRepository
    private lateinit var viewModel: DashboardViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        planRepository = FakePlanRepository()
        balanceLogRepository = FakeBalanceLogRepository()
        settingsRepository = FakeSettingsRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `auto-picks first plan when active plan is null`() = runTest(testDispatcher) {
        val plan = Plan(
            id = "plan1",
            type = PlanType.INTERNET,
            startAt = LocalDateTime.now(),
            endAt = LocalDateTime.now().plusDays(30),
            initialAmount = 100.0,
            unit = PlanUnit.GB
        )
        planRepository.addPlan(plan)

        viewModel = DashboardViewModel(planRepository, balanceLogRepository, settingsRepository)
        advanceUntilIdle()

        assertEquals("plan1", settingsRepository.activePlanId.first())
        assertTrue(viewModel.uiState.value is DashboardUiState.Success)
    }

    @Test
    fun `persists new active plan if current one is deleted`() = runTest(testDispatcher) {
        // Setup: Settings has "old_plan", but Repo only has "new_plan"
        settingsRepository.setActivePlanId("old_plan")

        val plan = Plan(
            id = "new_plan",
            type = PlanType.INTERNET,
            startAt = LocalDateTime.now(),
            endAt = LocalDateTime.now().plusDays(30),
            initialAmount = 100.0,
            unit = PlanUnit.GB
        )
        planRepository.addPlan(plan)

        viewModel = DashboardViewModel(planRepository, balanceLogRepository, settingsRepository)
        advanceUntilIdle()

        assertEquals("new_plan", settingsRepository.activePlanId.first())
    }
}

class FakePlanRepository : PlanRepository {
    private val plans = MutableStateFlow<List<Plan>>(emptyList())

    suspend fun addPlan(plan: Plan) {
        plans.value += plan
    }

    override fun getAllPlans(): Flow<List<Plan>> = plans

    override fun getPlan(id: String): Flow<Plan?> = plans.map { it.find { plan -> plan.id == id } }

    override suspend fun insertPlan(plan: Plan) {
        addPlan(plan)
    }
}

class FakeBalanceLogRepository : BalanceLogRepository {
    override fun getBalanceLogsForPlan(planId: String): Flow<List<BalanceLog>> = flow { emit(emptyList()) }
    override suspend fun insertLog(log: BalanceLog) {}
}

class FakeSettingsRepository : SettingsRepository {
    private val _activePlanId = MutableStateFlow<String?>(null)
    override val activePlanId: Flow<String?> = _activePlanId
    override val alertsEnabled: Flow<Boolean> = MutableStateFlow(true)
    override val weeklyReminderEnabled: Flow<Boolean> = MutableStateFlow(true)

    override suspend fun setActivePlanId(id: String?) {
        _activePlanId.value = id
    }

    override suspend fun setAlertsEnabled(enabled: Boolean) {}
    override suspend fun setWeeklyReminderEnabled(enabled: Boolean) {}
}
