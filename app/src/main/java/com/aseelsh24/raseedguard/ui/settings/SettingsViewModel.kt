package com.aseelsh24.raseedguard.ui.settings

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aseelsh24.raseedguard.data.repository.SettingsRepository
import com.aseelsh24.raseedguard.notifications.WorkScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val application: Application
) : ViewModel() {

    val alertsEnabled: StateFlow<Boolean> = settingsRepository.alertsEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = true
        )

    val weeklyReminderEnabled: StateFlow<Boolean> = settingsRepository.weeklyReminderEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = true
        )

    fun setAlertsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAlertsEnabled(enabled)
            if (enabled) {
                WorkScheduler.scheduleUsageAlerts(application)
            } else {
                WorkScheduler.cancelUsageAlerts(application)
            }
        }
    }

    fun setWeeklyReminderEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setWeeklyReminderEnabled(enabled)
            if (enabled) {
                WorkScheduler.scheduleWeeklyReminder(application)
            } else {
                WorkScheduler.cancelWeeklyReminder(application)
            }
        }
    }
}
