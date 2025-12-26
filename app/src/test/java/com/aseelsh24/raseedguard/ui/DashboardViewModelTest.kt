package com.aseelsh24.raseedguard.ui

import com.aseelsh24.raseedguard.core.*
import com.aseelsh24.raseedguard.data.repository.BalanceLogRepository
import com.aseelsh24.raseedguard.data.repository.PlanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
import com.aseelsh24.raseedguard.core.Unit as PlanUnit

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private lateinit var viewModel: DashboardViewModel
    private lateinit var planRepository: FakePlanRepository
    private lateinit var balanceLogRepository: FakeBalanceLogRepository
    private lateinit var settingsRepository: FakeSettingsRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        planRepository = FakePlanRepository()
        balanceLogRepository = FakeBalanceLogRepository()
        settingsRepository = FakeSettingsRepository()
        viewModel = DashboardViewModel(planRepository, balanceLogRepository, settingsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when no plans, ui state is Empty`() = runTest {
        // Arrange
        planRepository.setPlans(emptyList())

        // Act
        advanceUntilIdle()

        // Assert
        assertTrue(viewModel.uiState.value is DashboardUiState.Empty)
    }

    @Test
    fun `when plans exist, ui state is Success`() = runTest {
        // Arrange
        val plan = Plan(
            id = "1",
            type = PlanType.INTERNET,
            category = PlanCategory.MOBILE,
            startAt = LocalDateTime.now().minusDays(1),
            endAt = LocalDateTime.now().plusDays(29),
            initialAmount = 100.0,
            unit = PlanUnit.GB
        )
        planRepository.setPlans(listOf(plan))
        balanceLogRepository.setLogs(emptyList())

        // Act
        advanceUntilIdle() // Process flow emissions

        // Assert
        val state = viewModel.uiState.value
        assertTrue("State should be Success, but was $state", state is DashboardUiState.Success)
        if (state is DashboardUiState.Success) {
            assertEquals(plan, state.plan)
        }
    }
}

class FakePlanRepository : PlanRepository {
    // Expose plans so tests can inspect them if needed
    private val plansFlow = MutableStateFlow<List<Plan>>(emptyList())

    // Helper for tests to set state
    fun setPlans(plans: List<Plan>) {
        plansFlow.value = plans
    }

    // Helper to get current plans synchronously for assertions
    val plans: List<Plan>
        get() = plansFlow.value

    override fun getAllPlans(): Flow<List<Plan>> = plansFlow

    override fun getPlan(id: String): Flow<Plan?> {
        return plansFlow.map { plans -> plans.find { it.id == id } }
    }

    override suspend fun insertPlan(plan: Plan) {
        val currentPlans = plansFlow.value.toMutableList()
        val index = currentPlans.indexOfFirst { it.id == plan.id }
        if (index != -1) {
            currentPlans[index] = plan
        } else {
            currentPlans.add(plan)
        }
        plansFlow.value = currentPlans
    }

    override suspend fun deletePlan(id: String) {
        // no-op
    }
}

class FakeBalanceLogRepository : BalanceLogRepository {
    private val logsFlow = MutableStateFlow<List<BalanceLog>>(emptyList())

    fun setLogs(logs: List<BalanceLog>) {
        logsFlow.value = logs
    }

    override fun getBalanceLogsForPlan(planId: String) = logsFlow

    override suspend fun insertLog(log: BalanceLog) {
        // no-op
    }
}

class FakeSettingsRepository : com.aseelsh24.raseedguard.data.repository.SettingsRepository {
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
