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
            planRepository.getAllPlans().collect { plans ->
                if (plans.isEmpty()) {
                    _uiState.value = DashboardUiState.Empty
                } else {
                    // Pick the best plan to show (e.g. latest end date)
                    val activePlan = plans.sortedByDescending { it.endAt }.first()

                    // Now observe logs for this plan
                    balanceLogRepository.getBalanceLogsForPlan(activePlan.id).collect { logs ->
                        val prediction = usagePredictor.predict(activePlan, logs, LocalDateTime.now())
                        _uiState.value = DashboardUiState.Success(activePlan, prediction)
                    }
                }
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
