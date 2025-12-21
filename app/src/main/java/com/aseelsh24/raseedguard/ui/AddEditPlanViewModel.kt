package com.aseelsh24.raseedguard.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aseelsh24.raseedguard.core.Plan
import com.aseelsh24.raseedguard.core.PlanCategory
import com.aseelsh24.raseedguard.core.PlanType
import com.aseelsh24.raseedguard.core.Unit as PlanUnit
import com.aseelsh24.raseedguard.data.repository.PlanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

class AddEditPlanViewModel(
    savedStateHandle: SavedStateHandle,
    private val planRepository: PlanRepository
) : ViewModel() {

    private val planId: String? = savedStateHandle["planId"]

    private val _uiState = MutableStateFlow(AddEditPlanUiState())
    val uiState: StateFlow<AddEditPlanUiState> = _uiState.asStateFlow()

    init {
        if (planId != null) {
            loadPlan(planId)
        }
    }

    private fun loadPlan(id: String) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            val plan = planRepository.getPlan(id).first()
            if (plan != null) {
                _uiState.value = AddEditPlanUiState(
                    planId = plan.id,
                    type = plan.type,
                    category = plan.category,
                    startAt = plan.startAt,
                    endAt = plan.endAt,
                    initialAmount = plan.initialAmount.toString(),
                    unit = plan.unit,
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false) // Plan not found?
            }
        }
    }

    fun updateType(type: PlanType) {
        val currentUnit = _uiState.value.unit
        val newUnit = if (type == PlanType.VOICE) PlanUnit.MINUTES else {
             if (currentUnit == PlanUnit.MINUTES) PlanUnit.GB else currentUnit
        }
        val newCategory = if (type == PlanType.VOICE) PlanCategory.VOICE else {
             if (_uiState.value.category == PlanCategory.VOICE) PlanCategory.MOBILE else _uiState.value.category
        }
        _uiState.value = _uiState.value.copy(type = type, unit = newUnit, category = newCategory)
    }

    fun updateCategory(category: PlanCategory) {
        _uiState.value = _uiState.value.copy(category = category)
    }

    fun updateStartAt(startAt: LocalDateTime) {
        _uiState.value = _uiState.value.copy(startAt = startAt)
    }

    fun updateEndAt(endAt: LocalDateTime) {
        _uiState.value = _uiState.value.copy(endAt = endAt)
    }

    fun updateInitialAmount(amount: String) {
        _uiState.value = _uiState.value.copy(initialAmount = amount)
    }

    fun updateUnit(unit: PlanUnit) {
        _uiState.value = _uiState.value.copy(unit = unit)
    }

    private fun validate(): Boolean {
        val state = _uiState.value
        val amount = state.initialAmount.toDoubleOrNull()

        if (amount == null || amount <= 0) return false
        if (state.endAt.isBefore(state.startAt)) return false

        return true
    }

    fun savePlan(onSuccess: () -> Unit) {
        if (!validate()) return

        val state = _uiState.value
        val amount = state.initialAmount.toDoubleOrNull() ?: return

        val plan = Plan(
            id = state.planId ?: UUID.randomUUID().toString(),
            type = state.type,
            category = state.category,
            startAt = state.startAt,
            endAt = state.endAt,
            initialAmount = amount,
            unit = state.unit
        )

        viewModelScope.launch {
            planRepository.insertPlan(plan)
            onSuccess()
        }
    }
}

data class AddEditPlanUiState(
    val planId: String? = null,
    val type: PlanType = PlanType.INTERNET,
    val category: PlanCategory = PlanCategory.MOBILE,
    val startAt: LocalDateTime = LocalDateTime.now().with(LocalTime.MIN), // Start of today
    val endAt: LocalDateTime = LocalDateTime.now().plusDays(30).with(LocalTime.MAX), // End of 30 days
    val initialAmount: String = "",
    val unit: PlanUnit = PlanUnit.GB,
    val isLoading: Boolean = false
) {
    val isValid: Boolean
        get() {
            val amount = initialAmount.toDoubleOrNull()
            return amount != null && amount > 0 && !endAt.isBefore(startAt)
        }
}
