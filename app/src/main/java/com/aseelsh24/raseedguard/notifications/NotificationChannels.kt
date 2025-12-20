package com.aseelsh24.raseedguard.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationChannels {
    const val CHANNEL_USAGE_ALERTS = "usage_alerts"
    const val CHANNEL_REMINDERS = "reminders"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val usageChannel = NotificationChannel(
                CHANNEL_USAGE_ALERTS,
                "تنبيهات الاستهلاك", // Usage Alerts
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "تنبيهات عند اقتراب نفاذ الرصيد" // Alerts when balance is running low
            }

            val remindersChannel = NotificationChannel(
                CHANNEL_REMINDERS,
                "تذكيرات", // Reminders
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "تذكير أسبوعي لتسجيل الرصيد" // Weekly reminder to log balance
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannels(listOf(usageChannel, remindersChannel))
        }
    }
}
