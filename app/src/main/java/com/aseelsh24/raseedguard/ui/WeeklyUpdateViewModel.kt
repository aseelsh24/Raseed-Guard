package com.aseelsh24.raseedguard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aseelsh24.raseedguard.core.BalanceLog
import com.aseelsh24.raseedguard.data.repository.BalanceLogRepository
import com.aseelsh24.raseedguard.data.repository.PlanRepository
import com.aseelsh24.raseedguard.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class WeeklyUpdateViewModel(
    private val planRepository: PlanRepository,
    private val balanceLogRepository: BalanceLogRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeeklyUpdateUiState())
    val uiState: StateFlow<WeeklyUpdateUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.activePlanId.collect { id ->
                val planExists = if (id != null) {
                    // Check if plan actually exists
                    planRepository.getPlan(id).first() != null
                } else {
                    false
                }

                _uiState.value = _uiState.value.copy(
                    canSubmit = planExists,
                    errorMessage = if (!planExists) "No active plan found. Please create or select a plan." else null
                )
            }
        }
    }

    fun updateRemainingAmount(amount: String) {
        _uiState.value = _uiState.value.copy(remainingAmount = amount)
    }

    fun saveUpdate(onSuccess: () -> Unit) {
        val amount = _uiState.value.remainingAmount.toDoubleOrNull() ?: return

        viewModelScope.launch {
            val activeId = settingsRepository.activePlanId.first()
            if (activeId != null) {
                 val plan = planRepository.getPlan(activeId).first()
                 if (plan != null) {
                     val log = BalanceLog(
                         planId = activeId,
                         loggedAt = LocalDateTime.now(),
                         remainingAmount = amount
                     )

                     balanceLogRepository.insertLog(log)
                     onSuccess()
                 } else {
                      _uiState.value = _uiState.value.copy(errorMessage = "Active plan not found.")
                 }
            } else {
                 _uiState.value = _uiState.value.copy(errorMessage = "No active plan selected.")
            }
        }
    }
}

data class WeeklyUpdateUiState(
    val remainingAmount: String = "",
    val errorMessage: String? = null,
    val canSubmit: Boolean = false
)
