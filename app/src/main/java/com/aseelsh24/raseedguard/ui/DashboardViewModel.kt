package com.aseelsh24.raseedguard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aseelsh24.raseedguard.core.*
import com.aseelsh24.raseedguard.data.repository.BalanceLogRepository
import com.aseelsh24.raseedguard.data.repository.PlanRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime

data class PlanWithPrediction(
    val plan: Plan,
    val prediction: PredictionResult
)

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModel(
    private val planRepository: PlanRepository,
    private val balanceLogRepository: BalanceLogRepository
) : ViewModel() {

    private val usagePredictor = UsagePredictor()

    val uiState: StateFlow<DashboardUiState> = planRepository.getAllPlans()
        .flatMapLatest { plans ->
            if (plans.isEmpty()) {
                flowOf(DashboardUiState.Success(emptyList()))
            } else {
                val flows = plans.map { plan ->
                    balanceLogRepository.getLogsForPlan(plan.id).map { logs ->
                        val now = LocalDateTime.now()
                        val result = usagePredictor.predict(plan, logs, now)
                        PlanWithPrediction(plan, result)
                    }
                }
                combine(flows) { it.toList() }.map { DashboardUiState.Success(it) }
            }
        }
        .catch { e -> emit(DashboardUiState.Error(e.message ?: "Unknown error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardUiState.Loading
        )

    fun deletePlan(plan: Plan) {
        viewModelScope.launch {
            planRepository.deletePlan(plan)
        }
    }
}

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success(val plans: List<PlanWithPrediction>) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}
