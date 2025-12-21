package com.aseelsh24.raseedguard.ui.plans

import com.aseelsh24.raseedguard.core.Plan
import com.aseelsh24.raseedguard.core.PlanCategory
import com.aseelsh24.raseedguard.core.PlanType
import com.aseelsh24.raseedguard.core.Unit as PlanUnit
import com.aseelsh24.raseedguard.data.repository.PlanRepository
import com.aseelsh24.raseedguard.data.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class PlansViewModelTest {

    private lateinit var viewModel: PlansViewModel
    private lateinit var fakePlanRepository: FakePlanRepository
    private lateinit var fakeSettingsRepository: FakeSettingsRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        fakePlanRepository = FakePlanRepository()
        fakeSettingsRepository = FakeSettingsRepository()
        viewModel = PlansViewModel(fakePlanRepository, fakeSettingsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState correctly marks active plan`() = runTest {
        backgroundScope.launch { viewModel.uiState.collect {} }

        val plan1 = Plan(
            id = "plan1",
            type = PlanType.INTERNET,
            category = PlanCategory.MOBILE,
            startAt = LocalDateTime.now(),
            endAt = LocalDateTime.now().plusDays(30),
            initialAmount = 100.0,
            unit = PlanUnit.GB
        )
        val plan2 = Plan(
            id = "plan2",
            type = PlanType.VOICE,
            category = PlanCategory.VOICE,
            startAt = LocalDateTime.now(),
            endAt = LocalDateTime.now().plusDays(30),
            initialAmount = 500.0,
            unit = PlanUnit.MINUTES
        )

        fakePlanRepository.setPlans(listOf(plan1, plan2))
        fakeSettingsRepository.emitActivePlanId("plan1")

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.plans.size)

        val uiModel1 = state.plans.find { it.plan.id == "plan1" }
        assertTrue(uiModel1!!.isActive)

        val uiModel2 = state.plans.find { it.plan.id == "plan2" }
        assertFalse(uiModel2!!.isActive)
    }

    @Test
    fun `setActivePlan updates settings`() = runTest {
         backgroundScope.launch { viewModel.uiState.collect {} }

        val plan1 = Plan(
            id = "plan1",
            type = PlanType.INTERNET,
            category = PlanCategory.MOBILE,
            startAt = LocalDateTime.now(),
            endAt = LocalDateTime.now().plusDays(30),
            initialAmount = 100.0,
            unit = PlanUnit.GB
        )
        fakePlanRepository.setPlans(listOf(plan1))

        viewModel.setActivePlan("plan1")
        advanceUntilIdle()

        assertEquals("plan1", fakeSettingsRepository.activePlanId.first())
    }
}

class FakePlanRepository : PlanRepository {
    private val plansFlow = MutableStateFlow<List<Plan>>(emptyList())

    fun setPlans(plans: List<Plan>) {
        plansFlow.value = plans
    }

    override fun getAllPlans(): Flow<List<Plan>> = plansFlow

    override fun getPlan(id: String): Flow<Plan?> = flowOf(plansFlow.value.find { it.id == id })

    override suspend fun insertPlan(plan: Plan) {
        // no-op
    }
}

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
