package com.aseelsh24.raseedguard.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.aseelsh24.raseedguard.data.settings.ACTIVE_PLAN_ID
import com.aseelsh24.raseedguard.data.settings.settingsDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {
    override val activePlanId: Flow<String?> = context.settingsDataStore.data.map { preferences ->
        preferences[ACTIVE_PLAN_ID]
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
}
