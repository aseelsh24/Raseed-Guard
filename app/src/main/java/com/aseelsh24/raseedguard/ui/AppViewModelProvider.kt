package com.aseelsh24.raseedguard.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aseelsh24.raseedguard.RaseedGuardApplication

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            DashboardViewModel(
                raseedGuardApplication().container.planRepository,
                raseedGuardApplication().container.balanceLogRepository
            )
        }
        initializer {
            AddEditPlanViewModel(
                raseedGuardApplication().container.planRepository
            )
        }
        initializer {
            WeeklyUpdateViewModel(
                raseedGuardApplication().container.planRepository,
                raseedGuardApplication().container.balanceLogRepository
            )
        }
    }
}

fun CreationExtras.raseedGuardApplication(): RaseedGuardApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as RaseedGuardApplication)
