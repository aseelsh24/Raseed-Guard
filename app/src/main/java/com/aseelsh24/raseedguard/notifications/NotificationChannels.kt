package com.aseelsh24.raseedguard.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.aseelsh24.raseedguard.R

object NotificationChannels {
    const val CHANNEL_USAGE_ALERTS = "usage_alerts"
    const val CHANNEL_REMINDERS = "reminders"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val usageChannel = NotificationChannel(
                CHANNEL_USAGE_ALERTS,
                context.getString(R.string.channel_name_usage_alerts),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.channel_desc_usage_alerts)
            }

            val remindersChannel = NotificationChannel(
                CHANNEL_REMINDERS,
                context.getString(R.string.channel_name_reminders),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.channel_desc_reminders)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannels(listOf(usageChannel, remindersChannel))
        }
    }
}
