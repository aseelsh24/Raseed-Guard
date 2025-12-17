package com.aseelsh24.raseedguard.data.repository

import com.aseelsh24.raseedguard.core.Plan
import com.aseelsh24.raseedguard.data.local.dao.PlanDao
import com.aseelsh24.raseedguard.data.local.entities.PlanEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface PlanRepository {
    fun getAllPlans(): Flow<List<Plan>>
    fun getLatestPlan(): Flow<Plan?>
    suspend fun getPlanById(id: String): Plan?
    suspend fun insertPlan(plan: Plan)
    suspend fun updatePlan(plan: Plan)
    suspend fun deletePlan(plan: Plan)
}

class PlanRepositoryImpl(private val planDao: PlanDao) : PlanRepository {
    override fun getAllPlans(): Flow<List<Plan>> {
        return planDao.getAllPlans().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getLatestPlan(): Flow<Plan?> {
        return planDao.getLatestPlan().map { it?.toDomain() }
    }

    override suspend fun getPlanById(id: String): Plan? {
        return planDao.getPlanById(id)?.toDomain()
    }

    override suspend fun insertPlan(plan: Plan) {
        planDao.insertPlan(plan.toEntity())
    }

    override suspend fun updatePlan(plan: Plan) {
        planDao.updatePlan(plan.toEntity())
    }

    override suspend fun deletePlan(plan: Plan) {
        planDao.deletePlan(plan.toEntity())
    }
}

private fun PlanEntity.toDomain(): Plan {
    return Plan(
        id = id,
        type = type,
        startAt = startAt,
        endAt = endAt,
        initialAmount = initialAmount,
        unit = unit
    )
}

private fun Plan.toEntity(): PlanEntity {
    return PlanEntity(
        id = id,
        type = type,
        startAt = startAt,
        endAt = endAt,
        initialAmount = initialAmount,
        unit = unit
    )
}
