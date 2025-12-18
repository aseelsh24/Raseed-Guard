package com.aseelsh24.raseedguard.ui

import com.aseelsh24.raseedguard.core.Plan
import com.aseelsh24.raseedguard.core.PlanType
import com.aseelsh24.raseedguard.data.repository.PlanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import com.aseelsh24.raseedguard.core.Unit as PlanUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class AddEditPlanViewModelTest {

    private lateinit var viewModel: AddEditPlanViewModel
    private val planRepository = FakePlanRepository()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = AddEditPlanViewModel(planRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun savePlan_insertsPlanIntoRepository() = runTest {
        val initialAmount = 10.0
        val days = 30

        viewModel.savePlan(
            initialAmount = initialAmount,
            daysDuration = days
        )

        assertEquals(1, planRepository.plans.size)
        val savedPlan = planRepository.plans[0]
        assertEquals(initialAmount, savedPlan.initialAmount, 0.01)
        assertEquals(PlanType.INTERNET, savedPlan.type)
    }
}

class FakePlanRepository : PlanRepository {
    val plans = mutableListOf<Plan>()

    override fun getAllPlans(): Flow<List<Plan>> {
        return flowOf(plans)
    }

    override suspend fun insertPlan(plan: Plan) {
        plans.add(plan)
    }
}
