package com.aseelsh24.raseedguard.data.repository

import com.aseelsh24.raseedguard.core.Plan
import com.aseelsh24.raseedguard.data.dao.PlanDao
import com.aseelsh24.raseedguard.data.entity.toDomain
import com.aseelsh24.raseedguard.data.entity.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface PlanRepository {
    fun getAllPlans(): Flow<List<Plan>>
    fun getPlan(id: String): Flow<Plan?>
    suspend fun insertPlan(plan: Plan)
}

class PlanRepositoryImpl(private val planDao: PlanDao) : PlanRepository {
    override fun getAllPlans(): Flow<List<Plan>> {
        return planDao.getAllPlans().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getPlan(id: String): Flow<Plan?> {
        return planDao.getPlan(id).map { it?.toDomain() }
    }

    override suspend fun insertPlan(plan: Plan) {
        planDao.insertPlan(plan.toEntity())
    }
}
