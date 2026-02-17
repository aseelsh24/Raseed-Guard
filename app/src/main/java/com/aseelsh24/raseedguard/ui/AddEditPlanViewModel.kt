package com.aseelsh24.raseedguard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aseelsh24.raseedguard.core.BalanceLog
import com.aseelsh24.raseedguard.core.Plan
import com.aseelsh24.raseedguard.core.PlanType
import com.aseelsh24.raseedguard.core.Unit
import com.aseelsh24.raseedguard.data.repository.BalanceLogRepository
import com.aseelsh24.raseedguard.data.repository.PlanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddEditPlanViewModel @Inject constructor(
    private val planRepository: PlanRepository,
    private val balanceLogRepository: BalanceLogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditPlanUiState())
    val uiState: StateFlow<AddEditPlanUiState> = _uiState.asStateFlow()

    fun onPlanTypeChanged(type: PlanType) {
        _uiState.value = _uiState.value.copy(planType = type)
    }

    fun onStartDateChanged(date: LocalDateTime) {
        _uiState.value = _uiState.value.copy(
            startDate = date,
            startDateError = null
        )
        validateDates()
    }

    fun onEndDateChanged(date: LocalDateTime) {
        _uiState.value = _uiState.value.copy(
            endDate = date,
            endDateError = null
        )
        validateDates()
    }

    fun onInitialAmountChanged(amount: String) {
        _uiState.value = _uiState.value.copy(
            initialAmount = amount,
            initialAmountError = null
        )
    }

    fun onUnitChanged(unit: Unit) {
        _uiState.value = _uiState.value.copy(unit = unit)
    }

    private fun validateDates() {
        val state = _uiState.value
        if (state.startDate != null && state.endDate != null) {
            if (state.endDate.isBefore(state.startDate) || state.endDate.isEqual(state.startDate)) {
                _uiState.value = state.copy(
                    endDateError = "تاريخ الانتهاء يجب أن يكون بعد تاريخ البداية"
                )
            }
        }
    }

    fun savePlan(onSuccess: () -> Unit) {
        val state = _uiState.value

        // Validate all fields
        var hasError = false

        if (state.startDate == null) {
            _uiState.value = state.copy(startDateError = "تاريخ البداية مطلوب")
            hasError = true
        }

        if (state.endDate == null) {
            _uiState.value = state.copy(endDateError = "تاريخ الانتهاء مطلوب")
            hasError = true
        }

        val amount = state.initialAmount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _uiState.value = state.copy(initialAmountError = "الكمية الأولية يجب أن تكون أكبر من صفر")
            hasError = true
        }

        if (hasError) return

        // Save plan
        viewModelScope.launch {
            try {
                _uiState.value = state.copy(isSaving = true)

                val plan = Plan(
                    id = UUID.randomUUID().toString(),
                    type = state.planType,
                    startAt = state.startDate!!,
                    endAt = state.endDate!!,
                    initialAmount = amount!!,
                    unit = state.unit
                )

                planRepository.insertPlan(plan)

                // Create initial balance log
                val initialLog = BalanceLog(
                    planId = plan.id,
                    loggedAt = state.startDate!!,
                    remainingAmount = amount
                )
                balanceLogRepository.insertLog(initialLog)

                _uiState.value = state.copy(isSaving = false)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = state.copy(
                    isSaving = false,
                    saveError = e.message ?: "حدث خطأ أثناء الحفظ"
                )
            }
        }
    }
}

data class AddEditPlanUiState(
    val planType: PlanType = PlanType.INTERNET,
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null,
    val initialAmount: String = "",
    val unit: Unit = Unit.GB,
    val isSaving: Boolean = false,
    val startDateError: String? = null,
    val endDateError: String? = null,
    val initialAmountError: String? = null,
    val saveError: String? = null
)
