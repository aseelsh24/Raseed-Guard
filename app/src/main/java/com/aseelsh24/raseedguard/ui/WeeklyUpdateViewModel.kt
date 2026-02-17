package com.aseelsh24.raseedguard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aseelsh24.raseedguard.core.BalanceLog
import com.aseelsh24.raseedguard.core.Plan
import com.aseelsh24.raseedguard.data.repository.BalanceLogRepository
import com.aseelsh24.raseedguard.data.repository.PlanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class WeeklyUpdateViewModel @Inject constructor(
    private val planRepository: PlanRepository,
    private val balanceLogRepository: BalanceLogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeeklyUpdateUiState>(WeeklyUpdateUiState.Loading)
    val uiState: StateFlow<WeeklyUpdateUiState> = _uiState.asStateFlow()

    private val _selectedPlan = MutableStateFlow<Plan?>(null)
    private val _remainingAmount = MutableStateFlow("")
    val remainingAmount: StateFlow<String> = _remainingAmount.asStateFlow()

    private val _updateError = MutableStateFlow<String?>(null)
    val updateError: StateFlow<String?> = _updateError.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    init {
        loadPlans()
    }

    private fun loadPlans() {
        viewModelScope.launch {
            try {
                val plans = planRepository.getAllPlans().firstOrNull() ?: emptyList()
                if (plans.isEmpty()) {
                    _uiState.value = WeeklyUpdateUiState.Empty
                } else {
                    _uiState.value = WeeklyUpdateUiState.Success(plans)
                    // Select first plan by default
                    _selectedPlan.value = plans.firstOrNull()
                }
            } catch (e: Exception) {
                _uiState.value = WeeklyUpdateUiState.Error(e.message ?: "حدث خطأ أثناء تحميل الباقات")
            }
        }
    }

    fun onPlanSelected(plan: Plan) {
        _selectedPlan.value = plan
        _remainingAmount.value = ""
        _updateError.value = null
    }

    fun onRemainingAmountChanged(amount: String) {
        _remainingAmount.value = amount
        _updateError.value = null
    }

    fun saveUpdate(onSuccess: () -> Unit) {
        val plan = _selectedPlan.value
        if (plan == null) {
            _updateError.value = "يرجى اختيار باقة"
            return
        }

        val amount = _remainingAmount.value.toDoubleOrNull()
        if (amount == null || amount < 0) {
            _updateError.value = "يرجى إدخال كمية صحيحة"
            return
        }

        viewModelScope.launch {
            try {
                _isSaving.value = true

                // Get the latest log to check for increase
                val logs = balanceLogRepository.getBalanceLogsForPlan(plan.id).firstOrNull() ?: emptyList()
                val latestLog = logs.maxByOrNull { it.loggedAt }

                // Warn if amount increased
                if (latestLog != null && amount > latestLog.remainingAmount) {
                    // You could show a warning dialog here, but for now we'll just log it
                    // In a real app, you might want to ask for confirmation
                }

                val newLog = BalanceLog(
                    planId = plan.id,
                    loggedAt = LocalDateTime.now(),
                    remainingAmount = amount
                )

                balanceLogRepository.insertLog(newLog)

                _isSaving.value = false
                _remainingAmount.value = ""
                onSuccess()
            } catch (e: Exception) {
                _isSaving.value = false
                _updateError.value = e.message ?: "حدث خطأ أثناء الحفظ"
            }
        }
    }
}

sealed class WeeklyUpdateUiState {
    object Loading : WeeklyUpdateUiState()
    object Empty : WeeklyUpdateUiState()
    data class Success(val plans: List<Plan>) : WeeklyUpdateUiState()
    data class Error(val message: String) : WeeklyUpdateUiState()
}
