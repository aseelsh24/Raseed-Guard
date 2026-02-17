package com.aseelsh24.raseedguard.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.aseelsh24.raseedguard.core.Plan
import com.aseelsh24.raseedguard.core.PlanType
import com.aseelsh24.raseedguard.core.Unit
import com.aseelsh24.raseedguard.data.repository.BalanceLogRepository
import com.aseelsh24.raseedguard.data.repository.PlanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var planRepository: PlanRepository

    @Mock
    private lateinit var balanceLogRepository: BalanceLogRepository

    private lateinit var viewModel: DashboardViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState is Empty when no plans exist`() = runTest {
        // Given
        `when`(planRepository.getAllPlans()).thenReturn(flowOf(emptyList()))

        // When
        viewModel = DashboardViewModel(planRepository, balanceLogRepository)
        advanceUntilIdle()

        // Then
        assert(viewModel.uiState.value is DashboardUiState.Empty)
    }

    @Test
    fun `uiState is Success when plans exist`() = runTest {
        // Given
        val plan = Plan(
            id = "1",
            type = PlanType.INTERNET,
            startAt = LocalDateTime.now(),
            endAt = LocalDateTime.now().plusDays(30),
            initialAmount = 10.0,
            unit = Unit.GB
        )
        `when`(planRepository.getAllPlans()).thenReturn(flowOf(listOf(plan)))
        `when`(balanceLogRepository.getBalanceLogsForPlan(anyString())).thenReturn(flowOf(emptyList()))

        // When
        viewModel = DashboardViewModel(planRepository, balanceLogRepository)
        advanceUntilIdle()

        // Then
        assert(viewModel.uiState.value is DashboardUiState.Success)
    }

    @Test
    fun `refresh reloads data`() = runTest {
        // Given
        `when`(planRepository.getAllPlans()).thenReturn(flowOf(emptyList()))
        viewModel = DashboardViewModel(planRepository, balanceLogRepository)
        advanceUntilIdle()

        // When
        viewModel.refresh()
        advanceUntilIdle()

        // Then
        verify(planRepository, times(2)).getAllPlans()
    }
}
