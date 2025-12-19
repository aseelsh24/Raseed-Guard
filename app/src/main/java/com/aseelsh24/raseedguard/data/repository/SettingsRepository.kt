package com.aseelsh24.raseedguard.data.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val activePlanId: Flow<String?>
    suspend fun setActivePlanId(id: String?)
}
