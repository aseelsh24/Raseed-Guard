package com.aseelsh24.raseedguard.core

import org.junit.Assert.assertEquals
import org.junit.Test
import com.aseelsh24.raseedguard.core.Unit as PlanUnit

class UnitConverterTest {

    @Test
    fun `fromNormalizedMb converts MB correctly`() {
        val input = 500.0
        val result = UnitConverter.fromNormalizedMb(input, PlanUnit.MB)
        assertEquals(500.0, result, 0.001)
    }

    @Test
    fun `fromNormalizedMb converts GB correctly`() {
        val input = 20480.0 // 20 GB in MB
        val result = UnitConverter.fromNormalizedMb(input, PlanUnit.GB)
        assertEquals(20.0, result, 0.001)
    }

    @Test
    fun `fromNormalizedMb returns minutes as is`() {
        val input = 120.0
        val result = UnitConverter.fromNormalizedMb(input, PlanUnit.MINUTES)
        assertEquals(120.0, result, 0.001)
    }

    @Test
    fun `rateFromNormalizedMbPerDay converts MB rate correctly`() {
        val input = 50.0
        val result = UnitConverter.rateFromNormalizedMbPerDay(input, PlanUnit.MB)
        assertEquals(50.0, result, 0.001)
    }

    @Test
    fun `rateFromNormalizedMbPerDay converts GB rate correctly`() {
        val input = 2048.0 // 2 GB
        val result = UnitConverter.rateFromNormalizedMbPerDay(input, PlanUnit.GB)
        assertEquals(2.0, result, 0.001)
    }

    @Test
    fun `rateFromNormalizedMbPerDay returns minutes rate as is`() {
        val input = 15.0
        val result = UnitConverter.rateFromNormalizedMbPerDay(input, PlanUnit.MINUTES)
        assertEquals(15.0, result, 0.001)
    }
}
