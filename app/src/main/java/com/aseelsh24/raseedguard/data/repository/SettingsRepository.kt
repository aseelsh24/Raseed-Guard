package com.aseelsh24.raseedguard.data.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val activePlanId: Flow<String?>
    val alertsEnabled: Flow<Boolean>
    val weeklyReminderEnabled: Flow<Boolean>

    suspend fun setActivePlanId(id: String?)
    suspend fun setAlertsEnabled(enabled: Boolean)
    suspend fun setWeeklyReminderEnabled(enabled: Boolean)
}
