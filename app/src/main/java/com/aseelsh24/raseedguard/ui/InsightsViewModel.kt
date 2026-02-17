package com.aseelsh24.raseedguard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aseelsh24.raseedguard.core.PredictionResult
import com.aseelsh24.raseedguard.core.UsagePredictor
import com.aseelsh24.raseedguard.data.repository.BalanceLogRepository
import com.aseelsh24.raseedguard.data.repository.PlanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val planRepository: PlanRepository,
    private val balanceLogRepository: BalanceLogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<InsightsUiState>(InsightsUiState.Loading)
    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()

    private val usagePredictor = UsagePredictor()

    init {
        loadInsights()
    }

    private fun loadInsights() {
        viewModelScope.launch {
            try {
                planRepository.getAllPlans()
                    .collectLatest { plans ->
                        if (plans.isEmpty()) {
                            _uiState.value = InsightsUiState.Empty
                        } else {
                            val activePlan = plans.firstOrNull()
                            if (activePlan != null) {
                                balanceLogRepository.getBalanceLogsForPlan(activePlan.id)
                                    .collectLatest { logs ->
                                        val now = LocalDateTime.now()
                                        val prediction = usagePredictor.predict(activePlan, logs, now)

                                        // Calculate statistics
                                        val sortedLogs = logs.sortedBy { it.loggedAt }
                                        val totalConsumed = if (sortedLogs.isNotEmpty()) {
                                            val normalizedInitial = usagePredictor.normalize(activePlan.initialAmount, activePlan.unit)
                                            normalizedInitial - prediction.remainingNormalized
                                        } else {
                                            0.0
                                        }

                                        val consumptionPercentage = if (sortedLogs.isNotEmpty()) {
                                            val normalizedInitial = usagePredictor.normalize(activePlan.initialAmount, activePlan.unit)
                                            (totalConsumed / normalizedInitial) * 100
                                        } else {
                                            0.0
                                        }

                                        _uiState.value = InsightsUiState.Success(
                                            prediction = prediction,
                                            totalConsumed = totalConsumed,
                                            consumptionPercentage = consumptionPercentage,
                                            logsCount = logs.size
                                        )
                                    }
                            }
                        }
                    }
            } catch (e: Exception) {
                _uiState.value = InsightsUiState.Error(e.message ?: "حدث خطأ غير متوقع")
            }
        }
    }
}

sealed class InsightsUiState {
    object Loading : InsightsUiState()
    object Empty : InsightsUiState()
    data class Success(
        val prediction: PredictionResult,
        val totalConsumed: Double,
        val consumptionPercentage: Double,
        val logsCount: Int
    ) : InsightsUiState()
    data class Error(val message: String) : InsightsUiState()
}
