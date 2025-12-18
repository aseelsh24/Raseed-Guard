package com.aseelsh24.raseedguard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aseelsh24.raseedguard.core.Plan
import com.aseelsh24.raseedguard.core.PlanType
import com.aseelsh24.raseedguard.data.repository.PlanRepository
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import com.aseelsh24.raseedguard.core.Unit as PlanUnit

class AddEditPlanViewModel(
    private val planRepository: PlanRepository
) : ViewModel() {

    fun savePlan(
        initialAmount: Double,
        planType: PlanType = PlanType.INTERNET,
        startDate: LocalDateTime = LocalDateTime.now(),
        daysDuration: Int
    ) {
        viewModelScope.launch {
            val endDate = startDate.plusDays(daysDuration.toLong())
            val unit = if (planType == PlanType.INTERNET) PlanUnit.GB else PlanUnit.MINUTES

            val plan = Plan(
                id = UUID.randomUUID().toString(),
                type = planType,
                startAt = startDate,
                endAt = endDate,
                initialAmount = initialAmount,
                unit = unit
            )
            planRepository.insertPlan(plan)
        }
    }
}
