package com.example

import com.example.data.engine.ComparisonEngine
import com.example.data.engine.NaturalSearchParser
import com.example.data.local.PreloadedData
import com.example.data.models.UseCasePriority
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun comparisonEngine_evaluatesTwoBikes_producesValidReport() {
        val bikeA = PreloadedData.motorcycles.first { it.id == "yamaha-r15-v4" }
        val bikeB = PreloadedData.motorcycles.first { it.id == "ktm-rc-200" }

        val report = ComparisonEngine.evaluate(bikeA, bikeB, null, UseCasePriority.BEST_OVERALL)

        assertNotNull(report)
        assertTrue(report.overallScoreA in 40..100)
        assertTrue(report.overallScoreB in 40..100)
        assertTrue(report.categoryScores.isNotEmpty())
        assertTrue(report.writtenVerdict.isNotBlank())
    }

    @Test
    fun comparisonEngine_evaluatesThreeBikes_determinesWinners() {
        val bikeA = PreloadedData.motorcycles.first { it.id == "yamaha-r15-v4" }
        val bikeB = PreloadedData.motorcycles.first { it.id == "ktm-rc-200" }
        val bikeC = PreloadedData.motorcycles.first { it.id == "kawasaki-ninja-300" }

        val report = ComparisonEngine.evaluate(bikeA, bikeB, bikeC, UseCasePriority.PERFORMANCE)

        assertNotNull(report)
        assertNotNull(report.overallScoreC)
        // Ninja 300 has 39 HP vs 25 HP and 18.4 HP, should lead in raw output
        assertEquals(2, report.performanceWinnerIndex)
        // R15 has highest mileage (46 km/l), should win mileage
        assertEquals(0, report.mileageWinnerIndex)
    }

    @Test
    fun naturalSearchParser_parsesCategoryAndDisplacement() {
        val parsed = NaturalSearchParser.parse("Best 300cc sport bikes under 5 lakh")
        assertEquals("Sport", parsed.category)
        assertNotNull(parsed.minDisplacementCc)
        assertNotNull(parsed.maxDisplacementCc)
        assertTrue(parsed.maxPrice != null && parsed.maxPrice!! > 0)
    }
}
