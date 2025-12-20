package com.aseelsh24.raseedguard.notifications

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aseelsh24.raseedguard.R
import com.aseelsh24.raseedguard.RaseedGuardApplication
import com.aseelsh24.raseedguard.core.UsagePredictor
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class UsageAlertWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val application = applicationContext as RaseedGuardApplication
        val container = application.container

        val settingsRepository = container.settingsRepository
        val planRepository = container.planRepository
        val balanceLogRepository = container.balanceLogRepository

        // Check if alerts are enabled
        val alertsEnabled = settingsRepository.alertsEnabled.first()
        if (!alertsEnabled) {
            return Result.success()
        }

        // 1. Read activePlanId
        val activePlanId = settingsRepository.activePlanId.firstOrNull()
        if (activePlanId == null) {
            return Result.success()
        }

        // 2. Load plan + logs
        val plan = planRepository.getPlan(activePlanId).firstOrNull()
        if (plan == null) {
            return Result.success()
        }

        val logs = balanceLogRepository.getBalanceLogsForPlan(activePlanId).first()

        // 3. Compute prediction
        val predictor = UsagePredictor()
        val prediction = predictor.predict(plan, logs)

        // 4. Decide alert
        val decision = AlertPolicy.decideAlert(prediction)

        if (decision.shouldNotify) {
            sendNotification(decision)
        }

        return Result.success()
    }

    private fun sendNotification(decision: AlertDecision) {
        val context = applicationContext
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val titleBase = context.getString(R.string.notification_title_balance_alert)
        val riskText = when (decision.riskLevel) {
            com.aseelsh24.raseedguard.core.RiskLevel.CRITICAL -> context.getString(R.string.risk_level_critical_suffix)
            com.aseelsh24.raseedguard.core.RiskLevel.WARNING -> context.getString(R.string.risk_level_warning)
            else -> ""
        }
        val title = if (riskText.isNotEmpty()) "$titleBase: $riskText" else titleBase
        val message = context.getString(R.string.notification_message_balance_alert)

        val notification = NotificationCompat.Builder(context, NotificationChannels.CHANNEL_USAGE_ALERTS)
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(1, notification)
    }
}
