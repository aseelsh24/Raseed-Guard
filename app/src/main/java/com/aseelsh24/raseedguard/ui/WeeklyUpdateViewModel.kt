package com.aseelsh24.raseedguard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aseelsh24.raseedguard.core.BalanceLog
import com.aseelsh24.raseedguard.data.repository.BalanceLogRepository
import com.aseelsh24.raseedguard.data.repository.PlanRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class WeeklyUpdateViewModel(
    private val planRepository: PlanRepository,
    private val balanceLogRepository: BalanceLogRepository
) : ViewModel() {

    fun saveLog(remainingAmount: String) {
        val amount = remainingAmount.toDoubleOrNull() ?: return

        viewModelScope.launch {
            val plan = planRepository.getLatestPlan().first() ?: return@launch

            val log = BalanceLog(
                planId = plan.id,
                loggedAt = LocalDateTime.now(),
                remainingAmount = amount
            )

            balanceLogRepository.insertLog(log)
        }
    }
}
