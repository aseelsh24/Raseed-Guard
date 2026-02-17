package com.aseelsh24.raseedguard

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.aseelsh24.raseedguard.notification.NotificationHelper
import com.aseelsh24.raseedguard.notification.WorkScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class RaseedGuardApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        // Initialize notification channel
        NotificationHelper.createNotificationChannel(this)

        // Schedule background work
        WorkScheduler.scheduleDailyCheck(this)
        WorkScheduler.scheduleWeeklyReminder(this)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}

