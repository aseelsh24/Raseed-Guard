package com.aseelsh24.raseedguard.ui.weeklyupdate

import com.aseelsh24.raseedguard.core.BalanceLog
import com.aseelsh24.raseedguard.core.Plan
import com.aseelsh24.raseedguard.data.repository.BalanceLogRepository
import com.aseelsh24.raseedguard.data.repository.PlanRepository
import com.aseelsh24.raseedguard.data.repository.SettingsRepository
import com.aseelsh24.raseedguard.ui.WeeklyUpdateUiState
import com.aseelsh24.raseedguard.ui.WeeklyUpdateViewModel
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
class WeeklyUpdateViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var planRepository: FakePlanRepository
    private lateinit var balanceLogRepository: FakeBalanceLogRepository
    private lateinit var settingsRepository: FakeSettingsRepository
    private lateinit var viewModel: WeeklyUpdateViewModel

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
    fun `initial state has no error and cannot submit when no active plan`() = runTest(testDispatcher) {
        viewModel = WeeklyUpdateViewModel(planRepository, balanceLogRepository, settingsRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        // Initially it might take a tick to collect, but with advanceUntilIdle it should settle.
        // If activePlanId is null (default in FakeSettings), canSubmit should be false.
        assertFalse(state.canSubmit)
        assertEquals("No active plan found. Please create or select a plan.", state.errorMessage)
    }

    @Test
    fun `can submit when active plan exists`() = runTest(testDispatcher) {
        val plan = Plan(
            id = "plan1",
            name = "Test Plan",
            totalInternet = 100.0,
            totalMinutes = 100.0,
            startAt = LocalDateTime.now(),
            endAt = LocalDateTime.now().plusDays(30),
            price = 50.0
        )
        planRepository.addPlan(plan)
        settingsRepository.setActivePlanId("plan1")

        viewModel = WeeklyUpdateViewModel(planRepository, balanceLogRepository, settingsRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.canSubmit)
        assertNull(state.errorMessage)
    }

    @Test
    fun `saveUpdate does not insert log if no active plan`() = runTest(testDispatcher) {
        viewModel = WeeklyUpdateViewModel(planRepository, balanceLogRepository, settingsRepository)
        viewModel.updateRemainingAmount("50")

        var successCalled = false
        viewModel.saveUpdate { successCalled = true }
        advanceUntilIdle()

        assertFalse(successCalled)
        assertTrue(balanceLogRepository.logs.isEmpty())
        assertEquals("No active plan selected.", viewModel.uiState.value.errorMessage)
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

    override suspend fun updatePlan(plan: Plan) {}

    override suspend fun deletePlan(plan: Plan) {}
}

class FakeBalanceLogRepository : BalanceLogRepository {
    val logs = mutableListOf<BalanceLog>()

    override fun getBalanceLogsForPlan(planId: String): Flow<List<BalanceLog>> = flow { emit(logs.filter { it.planId == planId }) }

    override suspend fun insertLog(log: BalanceLog) {
        logs.add(log)
    }
}

class FakeSettingsRepository : SettingsRepository {
    private val _activePlanId = MutableStateFlow<String?>(null)
    override val activePlanId: Flow<String?> = _activePlanId

    override suspend fun setActivePlanId(id: String?) {
        _activePlanId.value = id
    }
}
