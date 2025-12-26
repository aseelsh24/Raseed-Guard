package com.aseelsh24.raseedguard.data.repository

import com.aseelsh24.raseedguard.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val activePlanId: Flow<String?>
    val alertsEnabled: Flow<Boolean>
    val weeklyReminderEnabled: Flow<Boolean>
    val themeMode: Flow<ThemeMode>
    val dynamicColorEnabled: Flow<Boolean>

    suspend fun setActivePlanId(id: String?)
    suspend fun setAlertsEnabled(enabled: Boolean)
    suspend fun setWeeklyReminderEnabled(enabled: Boolean)
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setDynamicColorEnabled(enabled: Boolean)
}
