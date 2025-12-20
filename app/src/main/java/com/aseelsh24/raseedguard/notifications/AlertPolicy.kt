package com.aseelsh24.raseedguard.notifications

import com.aseelsh24.raseedguard.core.PredictionResult
import com.aseelsh24.raseedguard.core.RiskLevel

data class AlertDecision(
    val shouldNotify: Boolean,
    val title: String,
    val message: String
)

object AlertPolicy {
    fun decideAlert(prediction: PredictionResult?): AlertDecision {
        if (prediction == null) {
            return AlertDecision(shouldNotify = false, title = "", message = "")
        }

        if (prediction.riskLevel == RiskLevel.SAFE) {
            return AlertDecision(shouldNotify = false, title = "", message = "")
        }

        val title = "تنبيه الرصيد" // Balance Alert
        val riskText = when (prediction.riskLevel) {
            RiskLevel.CRITICAL -> "مستوى حرج" // Critical Level
            RiskLevel.WARNING -> "تحذير" // Warning
            else -> ""
        }

        // We can add prediction date if available
        // Simple message: "Risk Level: WARNING. Depletion by: ..."
        // Arabic: "مستوى الخطر: تحذير"

        // Let's make a simple message.
        val message = "انتبه! استهلاكك الحالي قد يؤدي لنفاذ الرصيد قبل الموعد." // Caution! Your current usage may deplete balance before due date.

        return AlertDecision(
            shouldNotify = true,
            title = "$title: $riskText",
            message = message
        )
    }
}
