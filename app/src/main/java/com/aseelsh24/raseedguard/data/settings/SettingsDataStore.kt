package com.aseelsh24.raseedguard.data.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.settingsDataStore by preferencesDataStore(name = "settings")

val ACTIVE_PLAN_ID = stringPreferencesKey("active_plan_id")
val ALERTS_ENABLED = booleanPreferencesKey("alerts_enabled")
val WEEKLY_REMINDER_ENABLED = booleanPreferencesKey("weekly_reminder_enabled")
val THEME_MODE = stringPreferencesKey("theme_mode")
val DYNAMIC_COLOR_ENABLED = booleanPreferencesKey("dynamic_color_enabled")
