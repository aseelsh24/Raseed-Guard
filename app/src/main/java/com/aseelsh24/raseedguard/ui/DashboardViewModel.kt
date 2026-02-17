package com.aseelsh24.raseedguard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aseelsh24.raseedguard.core.*
import com.aseelsh24.raseedguard.data.repository.BalanceLogRepository
import com.aseelsh24.raseedguard.data.repository.PlanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
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
            try {
                planRepository.getAllPlans()
                    .collectLatest { plans ->
                        if (plans.isEmpty()) {
                            _uiState.value = DashboardUiState.Empty
                        } else {
                            // Take the first active plan for now
                            val activePlan = plans.firstOrNull()
                            if (activePlan != null) {
                                balanceLogRepository.getBalanceLogsForPlan(activePlan.id)
                                    .collectLatest { logs ->
                                        val now = LocalDateTime.now()
                                        val prediction = usagePredictor.predict(activePlan, logs, now)
                                        _uiState.value = DashboardUiState.Success(
                                            plan = activePlan,
                                            prediction = prediction
                                        )
                                    }
                            }
                        }
                    }
            } catch (e: Exception) {
                _uiState.value = DashboardUiState.Error(e.message ?: "حدث خطأ غير متوقع")
            }
        }
    }

    fun refresh() {
        loadData()
    }
}

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    object Empty : DashboardUiState()
    data class Success(val plan: Plan, val prediction: PredictionResult) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}
