package com.aseelsh24.raseedguard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aseelsh24.raseedguard.core.*
import com.aseelsh24.raseedguard.data.repository.BalanceLogRepository
import com.aseelsh24.raseedguard.data.repository.PlanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class DashboardViewModel(
    private val planRepository: PlanRepository,
    private val balanceLogRepository: BalanceLogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val usagePredictor = UsagePredictor()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // We assume for now we just pick the first plan to show in dashboard
            planRepository.getAllPlans()
                .flatMapLatest { plans ->
                    if (plans.isEmpty()) {
                        flowOf(DashboardUiState.Empty)
                    } else {
                        // For simplicity, take the first plan.
                        // In a real app, we might have a "selected" plan or a list.
                        val plan = plans.first()
                        balanceLogRepository.getBalanceLogsForPlan(plan.id)
                            .map { logs ->
                                val now = LocalDateTime.now()
                                val result = usagePredictor.predict(plan, logs, now)
                                DashboardUiState.Success(plan, result)
                            }
                    }
                }
                .collect { state ->
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
