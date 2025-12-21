package com.aseelsh24.raseedguard.core

import java.time.LocalDateTime
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UsagePredictorPredictionTest {

    private val predictor = UsagePredictor()
    private val now = LocalDateTime.now()

    @Test
    fun `Test A - Works with one log (baseline + first log)`() {
        // Plan: initialAmount = 5 GB, startAt = now-10 days, endAt = now+10 days
        val plan = Plan(
            id = "plan1",
            type = PlanType.INTERNET,
            category = PlanCategory.MOBILE,
            startAt = now.minusDays(10),
            endAt = now.plusDays(10),
            initialAmount = 5.0,
            unit = Unit.GB
        )

        // Logs: one log at now-5 days with remainingAmount = 4 GB
        // 5GB start (-10d), 4GB now (-5d). Consumed 1GB in 5 days. Rate = 0.2 GB/day.
        val logs = listOf(
            BalanceLog(
                planId = plan.id,
                loggedAt = now.minusDays(5),
                remainingAmount = 4.0
            )
        )

        val prediction = predictor.predict(plan, logs, now)

        // Expect: smoothedDailyRate > 0
        assertTrue("Daily rate should be > 0", prediction.smoothedDailyRate > 0)

        // 4GB remaining. Rate 0.2 GB/day. Days left = 20 days.
        // Depletion = now + 20 days.
        assertNotNull("Depletion date should be predicted", prediction.predictedDepletionAt)
    }

    @Test
    fun `Test B - Close timestamps still compute (no hour truncation)`() {
        val plan = Plan(
            id = "plan2",
            type = PlanType.INTERNET,
            category = PlanCategory.MOBILE,
            startAt = now.minusHours(1),
            endAt = now.plusDays(30),
            initialAmount = 1000.0,
            unit = Unit.MB
        )

        // baseline at t0 (startAt)
        // first log at t0 + 30 minutes.
        // Consumed 10MB in 30 minutes.
        val logs = listOf(
            BalanceLog(
                planId = plan.id,
                loggedAt = plan.startAt.plusMinutes(30),
                remainingAmount = 990.0
            )
        )

        val prediction = predictor.predict(plan, logs, now)

        assertTrue("Rate should be computed and > 0", prediction.smoothedDailyRate > 0)
    }
}
