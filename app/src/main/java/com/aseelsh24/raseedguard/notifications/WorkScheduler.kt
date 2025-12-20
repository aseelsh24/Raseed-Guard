package com.aseelsh24.raseedguard.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object WorkScheduler {

    fun scheduleUsageAlertWorker(context: Context) {
        // Minimum interval is 15 minutes
        val request = PeriodicWorkRequestBuilder<UsageAlertWorker>(15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "usage_alerts",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun scheduleUsageAlerts(context: Context) {
        scheduleUsageAlertWorker(context)
    }

    fun cancelUsageAlerts(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork("usage_alerts")
    }

    fun scheduleWeeklyReminderWorker(context: Context) {
        // Weekly periodic
        val request = PeriodicWorkRequestBuilder<WeeklyReminderWorker>(7, TimeUnit.DAYS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "weekly_reminder",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun scheduleWeeklyReminder(context: Context) {
        scheduleWeeklyReminderWorker(context)
    }

    fun cancelWeeklyReminder(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork("weekly_reminder")
    }
}
