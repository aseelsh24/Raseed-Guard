package com.aseelsh24.raseedguard.data.settings

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.settingsDataStore by preferencesDataStore(name = "settings")

val ACTIVE_PLAN_ID = stringPreferencesKey("active_plan_id")
