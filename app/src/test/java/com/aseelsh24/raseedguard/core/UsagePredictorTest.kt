package com.aseelsh24.raseedguard.core

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class UsagePredictorTest {

    private val predictor = UsagePredictor()
    private val now = LocalDateTime.now()
    private val planStart = now.minusDays(5)
    private val planEnd = now.plusDays(25) // 30 day plan, 5 days in

    @Test
    fun `timeRemainingToExpiry returns correct duration`() {
        val endAt = now.plusDays(2)
        val remaining = predictor.timeRemainingToExpiry(now, endAt)
        assertEquals(2, remaining.toDays())
    }

    @Test
    fun `timeRemainingToExpiry returns zero if already expired`() {
        val endAt = now.minusDays(1)
        val remaining = predictor.timeRemainingToExpiry(now, endAt)
        assertTrue(remaining.isZero)
    }

    @Test
    fun `dailyRateFromLogs returns null for fewer than 2 logs`() {
        val logs = listOf(
            BalanceLog("1", now.minusDays(1), 100.0)
        )
        assertNull(predictor.dailyRateFromLogs(logs, PlanUnit.MB))
    }

    @Test
    fun `dailyRateFromLogs calculates correct rate for 2 logs`() {
        // 100 MB at day 0
        // 80 MB at day 1
        // Consumed 20 MB in 1 day -> Rate 20 MB/day
        val logs = listOf(
            BalanceLog("1", now.minusDays(1), 100.0),
            BalanceLog("1", now, 80.0)
        )
        val rate = predictor.dailyRateFromLogs(logs, PlanUnit.MB)
        assertEquals(20.0, rate!!, 0.01)
    }

    @Test
    fun `dailyRateFromLogs applies EWMA smoothing`() {
        // Day 0: 100
        // Day 1: 80 (Rate 20)
        // Day 2: 70 (Rate 10)

        // Initial Rate (Day 0-1): 20
        // Next Interval Rate (Day 1-2): 10
        // Smoothed = 0.5 * 10 + 0.5 * 20 = 15

        val logs = listOf(
            BalanceLog("1", now.minusDays(2), 100.0),
            BalanceLog("1", now.minusDays(1), 80.0),
            BalanceLog("1", now, 70.0)
        )
        val rate = predictor.dailyRateFromLogs(logs, PlanUnit.MB)
        assertEquals(15.0, rate!!, 0.01)
    }

    @Test
    fun `dailyRateFromLogs ignores increasing remaining amount`() {
        // Day 0: 100
        // Day 1: 120 (Top up? Ignore this interval)
        // Day 2: 100 (Consumed 20 in 1 day from Day 1 to Day 2)

        // Interval 1 (Day 0-1): Increase, skip.
        // Interval 2 (Day 1-2): 120 -> 100. Diff 20. Days 1. Rate 20.

        // Since first valid interval determines initial rate.

        val logs = listOf(
            BalanceLog("1", now.minusDays(2), 100.0),
            BalanceLog("1", now.minusDays(1), 120.0),
            BalanceLog("1", now, 100.0)
        )
        val rate = predictor.dailyRateFromLogs(logs, PlanUnit.MB)
        assertEquals(20.0, rate!!, 0.01)
    }

    @Test
    fun `dailyRateFromLogs normalizes GB to MB`() {
        // 1 GB = 1024 MB
        // Consumed 0.1 GB = 102.4 MB
        val logs = listOf(
            BalanceLog("1", now.minusDays(1), 1.0),
            BalanceLog("1", now, 0.9)
        )
        val rate = predictor.dailyRateFromLogs(logs, PlanUnit.GB)
        assertEquals(102.4, rate!!, 0.01)
    }

    @Test
    fun `predictedDepletionAt returns correct date`() {
        // Remaining 100 MB, Rate 20 MB/day -> 5 days
        val remaining = 100.0
        val rate = 20.0
        val expectedDate = now.plusDays(5)

        val predicted = predictor.predictedDepletionAt(now, remaining, rate)

        // Allow slight difference due to milliseconds/seconds
        val diffSeconds = ChronoUnit.SECONDS.between(expectedDate, predicted)
        assertTrue(Math.abs(diffSeconds) < 60)
    }

    @Test
    fun `riskLevel returns SAFE if depletion after end date`() {
        val endAt = now.plusDays(10)
        val depletion = now.plusDays(11)
        assertEquals(RiskLevel.SAFE, predictor.riskLevel(endAt, depletion))
    }

    @Test
    fun `riskLevel returns WARNING if depletion before end date but gap more than 48h`() {
        val endAt = now.plusDays(10)
        val depletion = now.plusDays(7) // Gap 3 days (72h)
        assertEquals(RiskLevel.WARNING, predictor.riskLevel(endAt, depletion))
    }

    @Test
    fun `riskLevel returns CRITICAL if depletion before end date and gap less than 48h`() {
        val endAt = now.plusDays(10)
        val depletion = now.plusDays(9) // Gap 1 day (24h)
        assertEquals(RiskLevel.CRITICAL, predictor.riskLevel(endAt, depletion))
    }

    @Test
    fun `riskLevel returns SAFE if rate is 0 or null (depletion null)`() {
        val endAt = now.plusDays(10)
        assertEquals(RiskLevel.SAFE, predictor.riskLevel(endAt, null))
    }

    @Test
    fun `predict returns correct full result`() {
        val plan = Plan("1", PlanType.INTERNET, PlanCategory.MOBILE, planStart, planEnd, 3000.0, PlanUnit.MB)
        // 5 days ago: 3000
        // 4 days ago: 2900 (Rate 100)
        // ...
        // now: 2500 (Rate 100)

        val logs = listOf(
            BalanceLog("1", now.minusDays(5), 3000.0),
            BalanceLog("1", now.minusDays(4), 2900.0),
            BalanceLog("1", now, 2500.0)
        )

        // Interval 1: 3000->2900 in 1 day. Rate 100.
        // Interval 2: 2900->2500 in 4 days. Rate 400/4 = 100.
        // Smoothed: 0.5*100 + 0.5*100 = 100.

        val result = predictor.predict(plan, logs, now)

        assertEquals(2500.0, result.remainingNormalized, 0.01)
        assertEquals(25, result.daysUntilEnd)
        assertEquals(100.0, result.dailyRate, 0.01)

        // Remaining 2500, Rate 100 -> 25 days left.
        // Plan ends in 25 days.
        // Depletion exactly at end date. Should be SAFE.
        assertEquals(RiskLevel.SAFE, result.riskLevel)

        // Safe usage: 2500 / 25 = 100
        assertEquals(100.0, result.safeDailyUsageTarget, 0.01)
    }

     @Test
    fun `predict handles insufficient logs`() {
        val plan = Plan("1", PlanType.INTERNET, PlanCategory.MOBILE, planStart, planEnd, 3000.0, PlanUnit.MB)
        val logs = listOf(
            BalanceLog("1", now, 3000.0)
        )

        val result = predictor.predict(plan, logs, now)

        assertEquals(3000.0, result.remainingNormalized, 0.01)
        assertEquals(RiskLevel.SAFE, result.riskLevel)
        assertEquals(0.0, result.dailyRate, 0.01)
        assertNull(result.predictedDepletionAt)
    }
}
