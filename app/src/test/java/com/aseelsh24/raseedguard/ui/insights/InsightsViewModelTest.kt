package com.aseelsh24.raseedguard.ui.insights

import com.aseelsh24.raseedguard.core.BalanceLog
import com.aseelsh24.raseedguard.core.Plan
import com.aseelsh24.raseedguard.core.PlanType
import com.aseelsh24.raseedguard.core.Unit as PlanUnit
import com.aseelsh24.raseedguard.data.repository.BalanceLogRepository
import com.aseelsh24.raseedguard.data.repository.PlanRepository
import com.aseelsh24.raseedguard.data.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
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

@OptIn(ExperimentalCoroutinesApi::class)
class InsightsViewModelTest {

    private lateinit var viewModel: InsightsViewModel
    private lateinit var fakePlanRepository: FakePlanRepository
    private lateinit var fakeBalanceLogRepository: FakeBalanceLogRepository
    private lateinit var fakeSettingsRepository: FakeSettingsRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        fakePlanRepository = FakePlanRepository()
        fakeBalanceLogRepository = FakeBalanceLogRepository()
        fakeSettingsRepository = FakeSettingsRepository()
        viewModel = InsightsViewModel(
            planRepository = fakePlanRepository,
            balanceLogRepository = fakeBalanceLogRepository,
            settingsRepository = fakeSettingsRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState is NoActivePlan when no active plan is selected`() = runTest {
        backgroundScope.launch { viewModel.uiState.collect() }

        fakeSettingsRepository.emitActivePlanId(null)
        advanceUntilIdle()

        assertEquals(InsightsUiState.NoActivePlan, viewModel.uiState.value)
    }

    @Test
    fun `uiState is Success when active plan and logs exist`() = runTest {
        backgroundScope.launch { viewModel.uiState.collect() }

        val plan = Plan(
            id = "plan1",
            type = PlanType.INTERNET,
            category = com.aseelsh24.raseedguard.core.PlanCategory.MOBILE,
            startAt = LocalDateTime.now().minusDays(10),
            endAt = LocalDateTime.now().plusDays(20),
            initialAmount = 100.0,
            unit = PlanUnit.MB
        )
        fakePlanRepository.addPlan(plan)
        fakeSettingsRepository.emitActivePlanId("plan1")

        val logs = listOf(
            BalanceLog(planId = "plan1", loggedAt = LocalDateTime.now().minusDays(9), remainingAmount = 90.0),
            BalanceLog(planId = "plan1", loggedAt = LocalDateTime.now().minusDays(8), remainingAmount = 80.0)
        )
        fakeBalanceLogRepository.setLogs(logs)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("Expected Success state but was $state", state is InsightsUiState.Success)
        val successState = state as InsightsUiState.Success
        assertEquals(plan, successState.plan)
        assertEquals(logs, successState.logs)
        // Check prediction exists
        assertTrue(successState.prediction != null)
    }
}

// Simple Fakes

class FakeSettingsRepository : SettingsRepository {
    private val _activePlanId = MutableStateFlow<String?>(null)
    override val activePlanId: Flow<String?> = _activePlanId
    override val alertsEnabled: Flow<Boolean> = MutableStateFlow(true)
    override val weeklyReminderEnabled: Flow<Boolean> = MutableStateFlow(true)

    fun emitActivePlanId(id: String?) {
        _activePlanId.value = id
    }

    override suspend fun setActivePlanId(id: String?) {
        _activePlanId.value = id
    }

    override suspend fun setAlertsEnabled(enabled: Boolean) {}
    override suspend fun setWeeklyReminderEnabled(enabled: Boolean) {}
}

class FakePlanRepository : PlanRepository {
    private val plans = mutableMapOf<String, Plan>()

    fun addPlan(plan: Plan) {
        plans[plan.id] = plan
    }

    override fun getAllPlans(): Flow<List<Plan>> {
        return flowOf(plans.values.toList())
    }

    override fun getPlan(id: String): Flow<Plan?> {
        return flowOf(plans[id])
    }

    override suspend fun insertPlan(plan: Plan) {
        plans[plan.id] = plan
    }

    override suspend fun deletePlan(id: String) {
        plans.remove(id)
    }
}

class FakeBalanceLogRepository : BalanceLogRepository {
    private val logs = mutableListOf<BalanceLog>()

    fun setLogs(newLogs: List<BalanceLog>) {
        logs.clear()
        logs.addAll(newLogs)
    }

    override fun getBalanceLogsForPlan(planId: String): Flow<List<BalanceLog>> {
        return flowOf(logs.filter { it.planId == planId })
    }

    override suspend fun insertLog(log: BalanceLog) {
        logs.add(log)
    }
}
