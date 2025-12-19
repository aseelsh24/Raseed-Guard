package com.aseelsh24.raseedguard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aseelsh24.raseedguard.core.BalanceLog
import com.aseelsh24.raseedguard.data.repository.BalanceLogRepository
import com.aseelsh24.raseedguard.data.repository.PlanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class WeeklyUpdateViewModel(
    private val planRepository: PlanRepository,
    private val balanceLogRepository: BalanceLogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeeklyUpdateUiState())
    val uiState: StateFlow<WeeklyUpdateUiState> = _uiState.asStateFlow()

    fun updateRemainingAmount(amount: String) {
        _uiState.value = _uiState.value.copy(remainingAmount = amount)
    }

    fun saveUpdate(onSuccess: () -> Unit) {
        val amount = _uiState.value.remainingAmount.toDoubleOrNull() ?: return

        viewModelScope.launch {
            // Find the active plan. For simplicity, we take the first plan available.
            // In a real app, we'd filter for the currently active plan.
            val plans = planRepository.getAllPlans().firstOrNull()

            if (!plans.isNullOrEmpty()) {
                // Sorting to find the most relevant plan (e.g. latest end date)
                val activePlan = plans.sortedByDescending { it.endAt }.first()

                val log = BalanceLog(
                    planId = activePlan.id,
                    loggedAt = LocalDateTime.now(),
                    remainingAmount = amount
                )

                balanceLogRepository.insertLog(log)
                onSuccess()
            } else {
                // Handle case where no plan exists
                // TODO: Add error handling or user feedback
            }
        }
    }
}

data class WeeklyUpdateUiState(
    val remainingAmount: String = ""
)
