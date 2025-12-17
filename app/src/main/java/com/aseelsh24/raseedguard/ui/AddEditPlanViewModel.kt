package com.aseelsh24.raseedguard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aseelsh24.raseedguard.core.Plan
import com.aseelsh24.raseedguard.core.PlanType
import com.aseelsh24.raseedguard.core.Unit
import com.aseelsh24.raseedguard.data.repository.PlanRepository
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class AddEditPlanViewModel(
    private val planRepository: PlanRepository
) : ViewModel() {

    fun savePlan(
        type: PlanType,
        startDate: java.time.LocalDate,
        endDate: java.time.LocalDate,
        initialAmount: String,
        unit: Unit
    ) {
        val amount = initialAmount.toDoubleOrNull() ?: return

        val startAt = LocalDateTime.of(startDate, LocalTime.MIDNIGHT)
        val endAt = LocalDateTime.of(endDate, LocalTime.MAX)

        val plan = Plan(
            id = UUID.randomUUID().toString(),
            type = type,
            startAt = startAt,
            endAt = endAt,
            initialAmount = amount,
            unit = unit
        )

        viewModelScope.launch {
            planRepository.insertPlan(plan)
        }
    }
}
