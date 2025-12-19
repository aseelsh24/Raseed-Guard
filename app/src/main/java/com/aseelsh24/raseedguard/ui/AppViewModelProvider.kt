package com.aseelsh24.raseedguard.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aseelsh24.raseedguard.RaseedGuardApplication

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            DashboardViewModel(
                planRepository = raseedGuardApplication().container.planRepository,
                balanceLogRepository = raseedGuardApplication().container.balanceLogRepository
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
                balanceLogRepository = raseedGuardApplication().container.balanceLogRepository
            )
        }
    }
}

fun CreationExtras.raseedGuardApplication(): RaseedGuardApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as RaseedGuardApplication)
