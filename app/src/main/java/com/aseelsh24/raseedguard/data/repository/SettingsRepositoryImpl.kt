package com.aseelsh24.raseedguard.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.aseelsh24.raseedguard.data.settings.ACTIVE_PLAN_ID
import com.aseelsh24.raseedguard.data.settings.ALERTS_ENABLED
import com.aseelsh24.raseedguard.data.settings.DYNAMIC_COLOR_ENABLED
import com.aseelsh24.raseedguard.data.settings.THEME_MODE
import com.aseelsh24.raseedguard.data.settings.WEEKLY_REMINDER_ENABLED
import com.aseelsh24.raseedguard.data.settings.settingsDataStore
import com.aseelsh24.raseedguard.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {

    override val activePlanId: Flow<String?> = context.settingsDataStore.data
        .map { preferences ->
            preferences[ACTIVE_PLAN_ID]
        }

    override val alertsEnabled: Flow<Boolean> = context.settingsDataStore.data
        .map { preferences ->
            preferences[ALERTS_ENABLED] ?: true
        }

    override val weeklyReminderEnabled: Flow<Boolean> = context.settingsDataStore.data
        .map { preferences ->
            preferences[WEEKLY_REMINDER_ENABLED] ?: true
        }

    override val themeMode: Flow<ThemeMode> = context.settingsDataStore.data
        .map { preferences ->
            when (preferences[THEME_MODE]) {
                "light" -> ThemeMode.LIGHT
                "dark" -> ThemeMode.DARK
                else -> ThemeMode.SYSTEM
            }
        }

    override val dynamicColorEnabled: Flow<Boolean> = context.settingsDataStore.data
        .map { preferences ->
            preferences[DYNAMIC_COLOR_ENABLED] ?: false
        }

    override suspend fun setActivePlanId(id: String?) {
        context.settingsDataStore.edit { preferences ->
            if (id == null) {
                preferences.remove(ACTIVE_PLAN_ID)
            } else {
                preferences[ACTIVE_PLAN_ID] = id
            }
        }
    }

    override suspend fun setAlertsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[ALERTS_ENABLED] = enabled
        }
    }

    override suspend fun setWeeklyReminderEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[WEEKLY_REMINDER_ENABLED] = enabled
        }
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        context.settingsDataStore.edit { preferences ->
            preferences[THEME_MODE] = when (mode) {
                ThemeMode.LIGHT -> "light"
                ThemeMode.DARK -> "dark"
                ThemeMode.SYSTEM -> "system"
            }
        }
    }

    override suspend fun setDynamicColorEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[DYNAMIC_COLOR_ENABLED] = enabled
        }
    }
}
