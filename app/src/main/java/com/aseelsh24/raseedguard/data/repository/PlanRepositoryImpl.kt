package com.aseelsh24.raseedguard.data.repository

import com.aseelsh24.raseedguard.core.Plan
import com.aseelsh24.raseedguard.data.local.PlanDao
import com.aseelsh24.raseedguard.data.local.PlanEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlanRepositoryImpl(private val planDao: PlanDao) : PlanRepository {
    override fun getAllPlans(): Flow<List<Plan>> {
        return planDao.getAllPlans().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getPlanById(id: String): Plan? {
        return planDao.getPlanById(id)?.toDomain()
    }

    override suspend fun insertPlan(plan: Plan) {
        planDao.insertPlan(plan.toEntity())
    }

    override suspend fun deletePlanById(id: String) {
        planDao.deletePlanById(id)
    }

    private fun PlanEntity.toDomain(): Plan {
        return Plan(
            id = id,
            name = name,
            type = type,
            totalAmount = totalAmount,
            unit = unit,
            startDate = startDate,
            endDate = endDate
        )
    }

    private fun Plan.toEntity(): PlanEntity {
        return PlanEntity(
            id = id,
            name = name,
            type = type,
            totalAmount = totalAmount,
            unit = unit,
            startDate = startDate,
            endDate = endDate
        )
    }
}
