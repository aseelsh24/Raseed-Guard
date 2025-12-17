package com.aseelsh24.raseedguard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aseelsh24.raseedguard.core.*
import com.aseelsh24.raseedguard.core.Unit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

class DashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val usagePredictor = UsagePredictor()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // Mock Data
            val now = LocalDateTime.now()
            val plan = Plan(
                id = UUID.randomUUID().toString(),
                type = PlanType.INTERNET,
                startAt = now.minusDays(10),
                endAt = now.plusDays(20),
                initialAmount = 10.0, // 10 GB
                unit = Unit.GB
            )

            val logs = listOf(
                BalanceLog(plan.id, now.minusDays(10), 10.0), // 10 GB
                BalanceLog(plan.id, now.minusDays(5), 8.0),   // 8 GB (Consumed 2 GB in 5 days -> 0.4 GB/day)
                BalanceLog(plan.id, now.minusDays(1), 7.0)    // 7 GB (Consumed 1 GB in 4 days -> 0.25 GB/day)
            )

            // Rate:
            // Interval 1: 0.4 GB/day. Smoothed = 0.4
            // Interval 2: 0.25 GB/day. Smoothed = 0.5*0.25 + 0.5*0.4 = 0.125 + 0.2 = 0.325 GB/day

            val result = usagePredictor.predict(plan, logs, now)

            _uiState.value = DashboardUiState.Success(
                plan = plan,
                prediction = result
            )
        }
    }
}

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success(val plan: Plan, val prediction: PredictionResult) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}
