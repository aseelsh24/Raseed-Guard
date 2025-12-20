package com.aseelsh24.raseedguard.notifications

import com.aseelsh24.raseedguard.core.PredictionResult
import com.aseelsh24.raseedguard.core.RiskLevel
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class AlertPolicyTest {

    private fun createPrediction(riskLevel: RiskLevel): PredictionResult {
        return PredictionResult(
            remainingNormalized = 100.0,
            daysUntilEnd = 10,
            dailyRate = 5.0,
            smoothedDailyRate = 5.0,
            predictedDepletionAt = LocalDateTime.now().plusDays(20),
            riskLevel = riskLevel,
            safeDailyUsageTarget = 10.0
        )
    }

    @Test
    fun `decideAlert returns false when prediction is null`() {
        val decision = AlertPolicy.decideAlert(null)
        assertFalse(decision.shouldNotify)
    }

    @Test
    fun `decideAlert returns false when risk is SAFE`() {
        val prediction = createPrediction(RiskLevel.SAFE)
        val decision = AlertPolicy.decideAlert(prediction)
        assertFalse(decision.shouldNotify)
    }

    @Test
    fun `decideAlert returns true when risk is WARNING`() {
        val prediction = createPrediction(RiskLevel.WARNING)
        val decision = AlertPolicy.decideAlert(prediction)
        assertTrue(decision.shouldNotify)
    }

    @Test
    fun `decideAlert returns true when risk is CRITICAL`() {
        val prediction = createPrediction(RiskLevel.CRITICAL)
        val decision = AlertPolicy.decideAlert(prediction)
        assertTrue(decision.shouldNotify)
    }
}
