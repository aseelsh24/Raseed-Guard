package com.aseelsh24.raseedguard.notifications

import com.aseelsh24.raseedguard.core.PredictionResult
import com.aseelsh24.raseedguard.core.RiskLevel

data class AlertDecision(
    val shouldNotify: Boolean,
    val riskLevel: RiskLevel? = null
)

object AlertPolicy {
    fun decideAlert(prediction: PredictionResult?): AlertDecision {
        if (prediction == null) {
            return AlertDecision(shouldNotify = false)
        }

        if (prediction.riskLevel == RiskLevel.SAFE) {
            return AlertDecision(shouldNotify = false)
        }

        return AlertDecision(
            shouldNotify = true,
            riskLevel = prediction.riskLevel
        )
    }
}
