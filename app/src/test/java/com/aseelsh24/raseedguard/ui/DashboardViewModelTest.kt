package com.aseelsh24.raseedguard.ui

import com.aseelsh24.raseedguard.core.*
import com.aseelsh24.raseedguard.data.repository.BalanceLogRepository
import com.aseelsh24.raseedguard.data.repository.PlanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
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
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        planRepository = FakePlanRepository()
        balanceLogRepository = FakeBalanceLogRepository()
        viewModel = DashboardViewModel(planRepository, balanceLogRepository)
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
    private val plansFlow = MutableStateFlow<List<Plan>>(emptyList())

    fun setPlans(plans: List<Plan>) {
        plansFlow.value = plans
    }

    override fun getAllPlans() = plansFlow

    override suspend fun insertPlan(plan: Plan) {
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
