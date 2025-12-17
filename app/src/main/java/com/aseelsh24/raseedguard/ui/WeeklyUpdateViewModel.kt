package com.aseelsh24.raseedguard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aseelsh24.raseedguard.core.BalanceLog
import com.aseelsh24.raseedguard.core.Plan
import com.aseelsh24.raseedguard.data.repository.BalanceLogRepository
import com.aseelsh24.raseedguard.data.repository.PlanRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class WeeklyUpdateViewModel(
    private val planRepository: PlanRepository,
    private val balanceLogRepository: BalanceLogRepository
) : ViewModel() {

    val uiState: StateFlow<WeeklyUpdateUiState> = planRepository.getAllPlans()
        .map { plans -> WeeklyUpdateUiState.Success(plans) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = WeeklyUpdateUiState.Loading
        )

    fun saveLog(planId: String, remainingAmount: String) {
        val amount = remainingAmount.toDoubleOrNull() ?: return

        viewModelScope.launch {
            val log = BalanceLog(
                planId = planId,
                loggedAt = LocalDateTime.now(),
                remainingAmount = amount
            )
            balanceLogRepository.insertLog(log)
        }
    }
}

sealed class WeeklyUpdateUiState {
    object Loading : WeeklyUpdateUiState()
    data class Success(val plans: List<Plan>) : WeeklyUpdateUiState()
}
