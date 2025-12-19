package com.aseelsh24.raseedguard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aseelsh24.raseedguard.core.*
import com.aseelsh24.raseedguard.data.repository.BalanceLogRepository
import com.aseelsh24.raseedguard.data.repository.PlanRepository
import com.aseelsh24.raseedguard.data.repository.SettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModel(
    private val planRepository: PlanRepository,
    private val balanceLogRepository: BalanceLogRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val usagePredictor = UsagePredictor()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                planRepository.getAllPlans(),
                settingsRepository.activePlanId
            ) { plans, activePlanId ->
                if (plans.isEmpty()) {
                    Triple(null, null, false)
                } else {
                    val resolvedPlan = plans.find { it.id == activePlanId }
                        ?: plans.sortedByDescending { it.endAt }.first()

                    val shouldPersist = activePlanId != resolvedPlan.id
                    Triple(resolvedPlan, resolvedPlan.id, shouldPersist)
                }
            }.flatMapLatest { (resolvedPlan, resolvedId, shouldPersist) ->
                if (resolvedPlan == null || resolvedId == null) {
                    flowOf(DashboardUiState.Empty)
                } else {
                    if (shouldPersist) {
                        settingsRepository.setActivePlanId(resolvedId)
                    }
                    balanceLogRepository.getBalanceLogsForPlan(resolvedId).map { logs ->
                        val prediction = usagePredictor.predict(resolvedPlan, logs, LocalDateTime.now())
                        DashboardUiState.Success(resolvedPlan, prediction)
                    }
                }
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    object Empty : DashboardUiState()
    data class Success(val plan: Plan, val prediction: PredictionResult) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}
