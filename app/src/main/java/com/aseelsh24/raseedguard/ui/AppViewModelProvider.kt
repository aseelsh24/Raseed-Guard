package com.aseelsh24.raseedguard.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aseelsh24.raseedguard.RaseedGuardApplication
import com.aseelsh24.raseedguard.ui.insights.InsightsViewModel
import com.aseelsh24.raseedguard.ui.settings.SettingsViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            SettingsViewModel(
                settingsRepository = raseedGuardApplication().container.settingsRepository,
                application = raseedGuardApplication()
            )
        }
        initializer {
            InsightsViewModel(
                planRepository = raseedGuardApplication().container.planRepository,
                balanceLogRepository = raseedGuardApplication().container.balanceLogRepository,
                settingsRepository = raseedGuardApplication().container.settingsRepository
            )
        }
        initializer {
            DashboardViewModel(
                planRepository = raseedGuardApplication().container.planRepository,
                balanceLogRepository = raseedGuardApplication().container.balanceLogRepository,
                settingsRepository = raseedGuardApplication().container.settingsRepository
            )
        }
        initializer {
            AddEditPlanViewModel(
                savedStateHandle = createSavedStateHandle(),
                planRepository = raseedGuardApplication().container.planRepository
            )
        }
        initializer {
            WeeklyUpdateViewModel(
                planRepository = raseedGuardApplication().container.planRepository,
                balanceLogRepository = raseedGuardApplication().container.balanceLogRepository,
                settingsRepository = raseedGuardApplication().container.settingsRepository
            )
        }
    }
}

fun CreationExtras.raseedGuardApplication(): RaseedGuardApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as RaseedGuardApplication)
