package com.aseelsh24.raseedguard.core

import com.aseelsh24.raseedguard.core.Unit as PlanUnit

object UnitConverter {
    private const val MB_PER_GB = 1024.0

    /** Convert normalized (MB) amounts to the plan’s display unit. */
    fun fromNormalizedMb(valueInMb: Double, unit: PlanUnit): Double {
        return when (unit) {
            PlanUnit.MB -> valueInMb
            PlanUnit.GB -> valueInMb / MB_PER_GB
            PlanUnit.MINUTES -> valueInMb // minutes are not normalized to MB
        }
    }

    /** Convert normalized (MB/day) rates to display unit/day. */
    fun rateFromNormalizedMbPerDay(rateMbPerDay: Double, unit: PlanUnit): Double {
        return when (unit) {
            PlanUnit.MB -> rateMbPerDay
            PlanUnit.GB -> rateMbPerDay / MB_PER_GB
            PlanUnit.MINUTES -> rateMbPerDay
        }
    }
}
