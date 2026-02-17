package com.aseelsh24.raseedguard.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aseelsh24.raseedguard.core.RiskLevel
import com.aseelsh24.raseedguard.core.UsagePredictor
import com.aseelsh24.raseedguard.data.repository.BalanceLogRepository
import com.aseelsh24.raseedguard.data.repository.PlanRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDateTime

@HiltWorker
class DailyCheckWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val planRepository: PlanRepository,
    private val balanceLogRepository: BalanceLogRepository
) : CoroutineWorker(context, workerParams) {

    private val usagePredictor = UsagePredictor()

    override suspend fun doWork(): Result {
        return try {
            // Get all plans
            val plans = planRepository.getAllPlans().firstOrNull() ?: emptyList()

            plans.forEach { plan ->
                // Get logs for this plan
                val logs = balanceLogRepository.getBalanceLogsForPlan(plan.id).firstOrNull() ?: emptyList()

                if (logs.isNotEmpty()) {
                    // Make prediction
                    val now = LocalDateTime.now()
                    val prediction = usagePredictor.predict(plan, logs, now)

                    // Send notification based on risk level
                    when (prediction.riskLevel) {
                        RiskLevel.CRITICAL -> {
                            NotificationHelper.sendRiskNotification(
                                applicationContext,
                                RiskLevel.CRITICAL,
                                "رصيدك سينفد خلال ${prediction.daysUntilEnd} يوم! قلل استهلاكك اليومي."
                            )
                        }
                        RiskLevel.WARNING -> {
                            NotificationHelper.sendRiskNotification(
                                applicationContext,
                                RiskLevel.WARNING,
                                "استهلاكك أعلى من الحد الآمن. قلل إلى %.2f يومياً.".format(prediction.safeDailyUsageTarget)
                            )
                        }
                        RiskLevel.SAFE -> {
                            // No notification needed for safe status
                        }
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
