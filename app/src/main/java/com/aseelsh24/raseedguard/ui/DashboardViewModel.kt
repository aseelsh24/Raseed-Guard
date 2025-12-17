package com.aseelsh24.raseedguard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aseelsh24.raseedguard.core.*
import com.aseelsh24.raseedguard.data.repository.BalanceLogRepository
import com.aseelsh24.raseedguard.data.repository.PlanRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModel(
    private val planRepository: PlanRepository,
    private val balanceLogRepository: BalanceLogRepository
) : ViewModel() {

    private val usagePredictor = UsagePredictor()

    val uiState: StateFlow<DashboardUiState> = planRepository.getLatestPlan()
        .flatMapLatest { plan ->
            if (plan == null) {
                flowOf(DashboardUiState.Error("No active plan found. Please add a plan."))
            } else {
                balanceLogRepository.getLogsForPlan(plan.id).map { logs ->
                    val now = LocalDateTime.now()
                    val result = usagePredictor.predict(plan, logs, now)
                    DashboardUiState.Success(
                        plan = plan,
                        prediction = result
                    )
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardUiState.Loading
        )
}

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success(val plan: Plan, val prediction: PredictionResult) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}
