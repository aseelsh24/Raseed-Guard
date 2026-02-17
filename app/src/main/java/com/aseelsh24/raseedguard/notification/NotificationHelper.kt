package com.aseelsh24.raseedguard.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.aseelsh24.raseedguard.R
import com.aseelsh24.raseedguard.core.RiskLevel

object NotificationHelper {
    private const val CHANNEL_ID = "raseed_guard_alerts"
    private const val CHANNEL_NAME = "تنبيهات الباقات"
    private const val CHANNEL_DESCRIPTION = "تنبيهات حول استهلاك الباقة"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendRiskNotification(context: Context, riskLevel: RiskLevel, message: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Make sure this icon exists
            .setContentTitle(
                when (riskLevel) {
                    RiskLevel.SAFE -> "رصيدك آمن ✅"
                    RiskLevel.WARNING -> "تحذير: استهلاك مرتفع ⚠️"
                    RiskLevel.CRITICAL -> "تنبيه حرج: الرصيد ينفد! 🚨"
                }
            )
            .setContentText(message)
            .setPriority(
                when (riskLevel) {
                    RiskLevel.SAFE -> NotificationCompat.PRIORITY_LOW
                    RiskLevel.WARNING -> NotificationCompat.PRIORITY_DEFAULT
                    RiskLevel.CRITICAL -> NotificationCompat.PRIORITY_HIGH
                }
            )
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }

    fun sendWeeklyReminderNotification(context: Context) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("تذكير: حدّث رصيدك")
            .setContentText("حان وقت التحديث الأسبوعي لرصيدك")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }
}
