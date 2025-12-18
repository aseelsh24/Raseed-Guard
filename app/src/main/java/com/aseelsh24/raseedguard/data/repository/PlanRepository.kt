package com.aseelsh24.raseedguard.data.repository

import com.aseelsh24.raseedguard.core.Plan
import kotlinx.coroutines.flow.Flow

interface PlanRepository {
    fun getAllPlans(): Flow<List<Plan>>
    suspend fun getPlanById(id: String): Plan?
    suspend fun insertPlan(plan: Plan)
    suspend fun deletePlanById(id: String)
}
