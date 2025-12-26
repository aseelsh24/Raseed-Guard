package com.aseelsh24.raseedguard.ui.plans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aseelsh24.raseedguard.core.Plan
import com.aseelsh24.raseedguard.data.repository.PlanRepository
import com.aseelsh24.raseedguard.data.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlansViewModel(
    private val planRepository: PlanRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<PlansUiState> = combine(
        planRepository.getAllPlans(),
        settingsRepository.activePlanId
    ) { plans, activeId ->
        PlansUiState(
            plans = plans.map { plan ->
                PlanUiModel(
                    plan = plan,
                    isActive = plan.id == activeId
                )
            },
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlansUiState(isLoading = true)
    )

    fun setActivePlan(planId: String) {
        viewModelScope.launch {
            settingsRepository.setActivePlanId(planId)
        }
    }

    fun deletePlan(planId: String) {
        viewModelScope.launch {
            // Check if deleted plan is active
            val currentPlans = uiState.value.plans.map { it.plan }
            val isDeletedPlanActive = uiState.value.plans.find { it.plan.id == planId }?.isActive == true

            if (isDeletedPlanActive) {
                val candidate = currentPlans.firstOrNull { it.id != planId }
                settingsRepository.setActivePlanId(candidate?.id)
            }

            planRepository.deletePlan(planId)
        }
    }
}

data class PlansUiState(
    val plans: List<PlanUiModel> = emptyList(),
    val isLoading: Boolean = false
)

data class PlanUiModel(
    val plan: Plan,
    val isActive: Boolean
)
