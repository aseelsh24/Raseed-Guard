package com.aseelsh24.raseedguard.ui.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aseelsh24.raseedguard.core.BalanceLog
import com.aseelsh24.raseedguard.core.Plan
import com.aseelsh24.raseedguard.core.PredictionResult
import com.aseelsh24.raseedguard.core.UsagePredictor
import com.aseelsh24.raseedguard.data.repository.BalanceLogRepository
import com.aseelsh24.raseedguard.data.repository.PlanRepository
import com.aseelsh24.raseedguard.data.repository.SettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

sealed interface InsightsUiState {
    data object Loading : InsightsUiState
    data object NoActivePlan : InsightsUiState
    data class Success(
        val plan: Plan,
        val logs: List<BalanceLog>,
        val prediction: PredictionResult?
    ) : InsightsUiState
}

@OptIn(ExperimentalCoroutinesApi::class)
class InsightsViewModel(
    private val planRepository: PlanRepository,
    private val balanceLogRepository: BalanceLogRepository,
    settingsRepository: SettingsRepository
) : ViewModel() {

    private val usagePredictor = UsagePredictor()

    val uiState: StateFlow<InsightsUiState> = settingsRepository.activePlanId
        .flatMapLatest { planId ->
            if (planId == null) {
                flowOf(InsightsUiState.NoActivePlan)
            } else {
                combine(
                    planRepository.getPlan(planId),
                    balanceLogRepository.getBalanceLogsForPlan(planId)
                ) { plan, logs ->
                    if (plan == null) {
                        InsightsUiState.NoActivePlan
                    } else {
                        val prediction = usagePredictor.predict(plan, logs)
                        InsightsUiState.Success(plan, logs, prediction)
                    }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = InsightsUiState.Loading
        )
}
