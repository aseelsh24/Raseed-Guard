package com.aseelsh24.raseedguard.core

import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.abs
import com.aseelsh24.raseedguard.core.Unit as PlanUnit

class UsagePredictor {

    companion object {
        const val WARNING_THRESHOLD_HOURS = 48
        const val ALPHA = 0.5
    }

    fun timeRemainingToExpiry(now: LocalDateTime, endAt: LocalDateTime): Duration {
        if (now.isAfter(endAt)) {
            return Duration.ZERO
        }
        return Duration.between(now, endAt)
    }

    /**
     * Calculates the daily consumption rate based on the logs.
     * Returns null if there are fewer than 2 valid logs or if the time span is too small.
     */
    fun dailyRateFromLogs(logs: List<BalanceLog>, unit: PlanUnit): Double? {
        if (logs.size < 2) return null

        val sortedLogs = logs.sortedBy { it.loggedAt }

        // We calculate rate between the first and the last log to get an average over the period
        // OR we can calculate interval by interval.
        // For EWMA, we need the series of rates.
        // The prompt says "dailyRateFromLogs(logs): rate per day". This usually implies an average or latest rate.
        // However, the next function is ewmaSmoothedRate(previousRate, latestRate).
        // This implies we need to calculate rates between intervals.

        // Let's implement this to return the average rate across the provided logs range for now,
        // but for EWMA we will need something more iterative.
        // Wait, if I look at the requirements:
        // - dailyRateFromLogs(logs): rate per day
        // - ewmaSmoothedRate(previousRate, latestRate, alpha=0.5)

        // To compute a single smoothed rate from a list of logs, we probably need to iterate through them.

        var currentSmoothedRate: Double? = null

        for (i in 0 until sortedLogs.size - 1) {
            val start = sortedLogs[i]
            val end = sortedLogs[i+1]

            // Normalize amounts
            val startAmount = normalize(start.remainingAmount, unit)
            val endAmount = normalize(end.remainingAmount, unit)

            // Check for increasing remaining amount (top-up or error)
            if (endAmount > startAmount) {
                // Requirement: remaining increases => ignore that interval
                continue
            }

            val durationHours = Duration.between(start.loggedAt, end.loggedAt).toHours()

            // Requirement: zero/negative time interval => ignore
            // Also practically, if duration is very small, rate might be huge. Let's ignore if < 1 hour?
            // "zero/negative time interval => ignore"
            if (durationHours <= 0) {
                 continue
            }

            val consumed = startAmount - endAmount
            val days = durationHours / 24.0
            val intervalRate = consumed / days

            // Requirement: rate <= 0 => treat as no depletion prediction.
            // (If consumed is 0, rate is 0).

            if (currentSmoothedRate == null) {
                currentSmoothedRate = intervalRate
            } else {
                // currentSmoothedRate is not null here
                currentSmoothedRate = ewmaSmoothedRate(currentSmoothedRate ?: intervalRate, intervalRate)
            }
        }

        return currentSmoothedRate
    }

    fun ewmaSmoothedRate(previousRate: Double, latestRate: Double, alpha: Double = ALPHA): Double {
        return (alpha * latestRate) + ((1 - alpha) * previousRate)
    }

    fun predictedDepletionAt(now: LocalDateTime, remaining: Double, smoothedRate: Double): LocalDateTime? {
        if (smoothedRate <= 0) return null

        val daysLeft = remaining / smoothedRate
        // Convert days to seconds for better precision
        val secondsLeft = (daysLeft * 24 * 3600).toLong()

        return now.plusSeconds(secondsLeft)
    }

    fun riskLevel(endAt: LocalDateTime, predictedDepletionAt: LocalDateTime?): RiskLevel {
        if (predictedDepletionAt == null) return RiskLevel.SAFE // rate <= 0 case

        // SAFE if depletionAt >= endAt
        if (!predictedDepletionAt.isBefore(endAt)) {
            return RiskLevel.SAFE
        }

        // WARNING if depletionAt < endAt and (endAt - depletionAt) > 48h
        // CRITICAL if depletionAt < endAt and (endAt - depletionAt) <= 48h

        val gapHours = Duration.between(predictedDepletionAt, endAt).toHours()

        return if (gapHours <= WARNING_THRESHOLD_HOURS) {
            RiskLevel.CRITICAL
        } else {
            RiskLevel.WARNING
        }
    }

    fun recommendedSafeDailyUsage(remaining: Double, daysUntilEnd: Double): Double {
        if (daysUntilEnd <= 0) return 0.0
        return remaining / daysUntilEnd
    }

    fun normalize(amount: Double, unit: PlanUnit): Double {
        return when (unit) {
            PlanUnit.MB -> amount
            PlanUnit.GB -> amount * 1024
            PlanUnit.MINUTES -> amount
        }
    }

    fun predict(plan: Plan, logs: List<BalanceLog>, now: LocalDateTime = LocalDateTime.now()): PredictionResult {
        // Normalize plan details
        val normalizedInitial = normalize(plan.initialAmount, plan.unit)

        // Normalize logs and find the latest log
        val sortedLogs = logs.sortedBy { it.loggedAt }

        // Find latest log before or at 'now' to determine remaining amount
        // If there are logs in the future, we should probably ignore them for "current status"
        // but the prompt doesn't specify. Assuming logs are valid.
        val latestLog = sortedLogs.lastOrNull { !it.loggedAt.isAfter(now) }

        // If no logs yet, use initial amount?
        // "Edge cases: <2 logs => cannot compute rate (return null/0 and mark as SAFE...)"
        // But we still need remainingNormalized and others.

        val remainingNormalized = if (latestLog != null) {
            normalize(latestLog.remainingAmount, plan.unit)
        } else {
            normalizedInitial
        }

        val daysUntilEnd = ChronoUnit.DAYS.between(now, plan.endAt).coerceAtLeast(0)

        val rate = dailyRateFromLogs(sortedLogs, plan.unit)

        val safeUsage = recommendedSafeDailyUsage(remainingNormalized, daysUntilEnd.toDouble())

        if (rate == null || rate <= 0) {
            return PredictionResult(
                remainingNormalized = remainingNormalized,
                daysUntilEnd = daysUntilEnd,
                dailyRate = 0.0,
                smoothedDailyRate = 0.0,
                predictedDepletionAt = null,
                riskLevel = RiskLevel.SAFE,
                safeDailyUsageTarget = safeUsage
            )
        }

        val depletionAt = predictedDepletionAt(now, remainingNormalized, rate)
        val risk = riskLevel(plan.endAt, depletionAt)

        return PredictionResult(
            remainingNormalized = remainingNormalized,
            daysUntilEnd = daysUntilEnd,
            dailyRate = rate, // The function calculates the smoothed rate effectively
            smoothedDailyRate = rate,
            predictedDepletionAt = depletionAt,
            riskLevel = risk,
            safeDailyUsageTarget = safeUsage
        )
    }
}
