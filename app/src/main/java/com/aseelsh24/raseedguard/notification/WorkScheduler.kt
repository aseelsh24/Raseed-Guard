package com.aseelsh24.raseedguard.notification

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object WorkScheduler {

    private const val DAILY_CHECK_WORK_NAME = "daily_check_work"
    private const val WEEKLY_REMINDER_WORK_NAME = "weekly_reminder_work"

    fun scheduleDailyCheck(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyCheckWorker>(
            1, TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .setInitialDelay(1, TimeUnit.HOURS) // Start after 1 hour
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            DAILY_CHECK_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            dailyWorkRequest
        )
    }

    fun scheduleWeeklyReminder(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val weeklyWorkRequest = PeriodicWorkRequestBuilder<WeeklyReminderWorker>(
            7, TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .setInitialDelay(7, TimeUnit.DAYS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WEEKLY_REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            weeklyWorkRequest
        )
    }

    fun cancelAllWork(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(DAILY_CHECK_WORK_NAME)
        WorkManager.getInstance(context).cancelUniqueWork(WEEKLY_REMINDER_WORK_NAME)
    }
}
