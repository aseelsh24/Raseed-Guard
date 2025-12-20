package com.aseelsh24.raseedguard.notifications

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aseelsh24.raseedguard.RaseedGuardApplication
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class WeeklyReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val application = applicationContext as RaseedGuardApplication
        val settingsRepository = application.container.settingsRepository

        // Check if reminders are enabled
        val weeklyReminderEnabled = settingsRepository.weeklyReminderEnabled.first()
        if (!weeklyReminderEnabled) {
            return Result.success()
        }

        // Check activePlanId exists (optional but good practice)
        val activePlanId = settingsRepository.activePlanId.firstOrNull()

        // Even if no active plan, we might want to remind them to create one?
        // But prompt says "Check activePlanId exists (optional)"
        if (activePlanId != null) {
            sendNotification()
        }

        return Result.success()
    }

    private fun sendNotification() {
        val context = applicationContext
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val title = context.getString(com.aseelsh24.raseedguard.R.string.notification_title_weekly_reminder)
        val message = context.getString(com.aseelsh24.raseedguard.R.string.notification_message_weekly_reminder)

        val notification = NotificationCompat.Builder(context, NotificationChannels.CHANNEL_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_menu_edit)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(2, notification)
    }
}
