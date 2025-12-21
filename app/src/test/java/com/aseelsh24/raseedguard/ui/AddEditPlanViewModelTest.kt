package com.aseelsh24.raseedguard.ui

import androidx.lifecycle.SavedStateHandle
import com.aseelsh24.raseedguard.core.Plan
import com.aseelsh24.raseedguard.core.PlanType
import com.aseelsh24.raseedguard.core.Unit as PlanUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
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
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditPlanViewModelTest {

    private lateinit var repository: FakePlanRepository
    private lateinit var viewModel: AddEditPlanViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakePlanRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `validation fails when amount is invalid`() = runTest {
        viewModel = AddEditPlanViewModel(SavedStateHandle(), repository)

        viewModel.updateInitialAmount("")
        assertFalse(viewModel.uiState.value.isValid)

        viewModel.updateInitialAmount("0")
        assertFalse(viewModel.uiState.value.isValid)

        viewModel.updateInitialAmount("-10")
        assertFalse(viewModel.uiState.value.isValid)
    }

    @Test
    fun `validation fails when end date is before start date`() = runTest {
        viewModel = AddEditPlanViewModel(SavedStateHandle(), repository)

        val now = LocalDateTime.now()
        viewModel.updateStartAt(now)
        viewModel.updateEndAt(now.minusDays(1))
        viewModel.updateInitialAmount("10")

        assertFalse(viewModel.uiState.value.isValid)
    }

    @Test
    fun `save plan creates new plan when no id provided`() = runTest {
        viewModel = AddEditPlanViewModel(SavedStateHandle(), repository)

        viewModel.updateInitialAmount("10")
        viewModel.updateType(PlanType.INTERNET)
        viewModel.updateUnit(PlanUnit.GB)

        var saved = false
        viewModel.savePlan { saved = true }
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(saved)
        assertEquals(1, repository.plans.size)
        assertEquals(10.0, repository.plans.first().initialAmount, 0.01)
    }

    @Test
    fun `load plan prefills state and updates existing plan`() = runTest {
        val existingPlan = Plan(
            id = "test-id",
            type = PlanType.VOICE,
            category = com.aseelsh24.raseedguard.core.PlanCategory.VOICE,
            startAt = LocalDateTime.now().minusDays(5).with(LocalTime.MIN),
            endAt = LocalDateTime.now().plusDays(5).with(LocalTime.MAX),
            initialAmount = 500.0,
            unit = PlanUnit.MINUTES
        )
        repository.insertPlan(existingPlan)

        val savedStateHandle = SavedStateHandle(mapOf("planId" to "test-id"))
        viewModel = AddEditPlanViewModel(savedStateHandle, repository)

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("test-id", state.planId)
        assertEquals("500.0", state.initialAmount)
        assertEquals(PlanType.VOICE, state.type)
        assertEquals(PlanUnit.MINUTES, state.unit)

        // Update amount
        viewModel.updateInitialAmount("600")
        var saved = false
        viewModel.savePlan { saved = true }
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(saved)
        assertEquals(1, repository.plans.size)
        assertEquals(600.0, repository.plans.first().initialAmount, 0.01)
        assertEquals("test-id", repository.plans.first().id)
    }
}
