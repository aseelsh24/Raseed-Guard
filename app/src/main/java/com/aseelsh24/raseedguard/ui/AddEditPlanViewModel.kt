package com.aseelsh24.raseedguard.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aseelsh24.raseedguard.core.Plan
import com.aseelsh24.raseedguard.core.PlanType
import com.aseelsh24.raseedguard.core.Unit as PlanUnit
import com.aseelsh24.raseedguard.data.repository.PlanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

class AddEditPlanViewModel(
    private val planRepository: PlanRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val planId: String? = savedStateHandle[Destinations.PLAN_ID_ARG]

    private val _uiState = MutableStateFlow(AddEditPlanUiState())
    val uiState: StateFlow<AddEditPlanUiState> = _uiState.asStateFlow()

    init {
        if (planId != null) {
            viewModelScope.launch {
                val plan = planRepository.getPlanById(planId)
                if (plan != null) {
                    _uiState.value = AddEditPlanUiState(
                        initialAmount = plan.initialAmount.toString(),
                        type = plan.type,
                        unit = plan.unit,
                        startDate = plan.startAt.toLocalDate().toString(),
                        endDate = plan.endAt.toLocalDate().toString(),
                        isEditing = true
                    )
                }
            }
        }
    }

    fun savePlan(
        type: PlanType,
        startDate: java.time.LocalDate,
        endDate: java.time.LocalDate,
        initialAmount: String,
        unit: PlanUnit
    ) {
        val amount = initialAmount.toDoubleOrNull() ?: return

        val startAt = LocalDateTime.of(startDate, LocalTime.MIDNIGHT)
        val endAt = LocalDateTime.of(endDate, LocalTime.MAX)

        viewModelScope.launch {
            if (planId != null) {
                // Update
                val updatedPlan = Plan(
                    id = planId,
                    type = type,
                    startAt = startAt,
                    endAt = endAt,
                    initialAmount = amount,
                    unit = unit
                )
                planRepository.updatePlan(updatedPlan)
            } else {
                // Insert
                val plan = Plan(
                    id = UUID.randomUUID().toString(),
                    type = type,
                    startAt = startAt,
                    endAt = endAt,
                    initialAmount = amount,
                    unit = unit
                )
                planRepository.insertPlan(plan)
            }
        }
    }
}

data class AddEditPlanUiState(
    val initialAmount: String = "",
    val type: PlanType = PlanType.INTERNET,
    val unit: PlanUnit = PlanUnit.GB,
    val startDate: String = java.time.LocalDate.now().toString(),
    val endDate: String = java.time.LocalDate.now().plusDays(30).toString(),
    val isEditing: Boolean = false
)
