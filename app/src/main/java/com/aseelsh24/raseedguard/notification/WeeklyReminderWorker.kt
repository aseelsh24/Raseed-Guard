package com.aseelsh24.raseedguard.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class WeeklyReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            NotificationHelper.sendWeeklyReminderNotification(applicationContext)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
