package com.aseelsh24.raseedguard

import android.app.Application
import com.aseelsh24.raseedguard.notifications.NotificationChannels
import com.aseelsh24.raseedguard.notifications.WorkScheduler

class RaseedGuardApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)

        NotificationChannels.createNotificationChannels(this)
        WorkScheduler.scheduleUsageAlertWorker(this)
        WorkScheduler.scheduleWeeklyReminderWorker(this)
    }
}
