package com.aseelsh24.raseedguard.ui

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
import java.util.UUID

class AddEditPlanViewModel(
    private val planRepository: PlanRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditPlanUiState())
    val uiState: StateFlow<AddEditPlanUiState> = _uiState.asStateFlow()

    fun updateInitialAmount(amount: String) {
        _uiState.value = _uiState.value.copy(initialAmount = amount)
    }

    fun updateUnit(unit: PlanUnit) {
        _uiState.value = _uiState.value.copy(unit = unit)
    }

    fun savePlan(onSuccess: () -> Unit) {
        val amount = _uiState.value.initialAmount.toDoubleOrNull() ?: return
        val now = LocalDateTime.now()
        // Assuming 30 days for now as UI doesn't have start/end inputs yet
        val start = now
        val end = now.plusDays(30)

        val plan = Plan(
            id = UUID.randomUUID().toString(),
            type = PlanType.INTERNET, // Defaulting to Internet
            startAt = start,
            endAt = end,
            initialAmount = amount,
            unit = _uiState.value.unit
        )

        viewModelScope.launch {
            planRepository.insertPlan(plan)
            onSuccess()
        }
    }
}

data class AddEditPlanUiState(
    val initialAmount: String = "",
    val unit: PlanUnit = PlanUnit.GB
)
