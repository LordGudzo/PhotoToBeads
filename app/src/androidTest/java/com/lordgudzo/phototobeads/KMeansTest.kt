package com.lordgudzo.phototobeads

import com.lordgudzo.phototobeads.domain.imageprocessing.KMeans
import com.lordgudzo.phototobeads.domain.model.LabPoint
import org.junit.Assert.*
import org.junit.Test
import kotlin.math.abs

class KMeansTest {

    // Helper: check if two numbers are almost equal
    // We use epsilon = 1.5 because LAB values can have small errors
    private fun almostEqual(a: Double, b: Double, eps: Double = 1.5): Boolean {
        return abs(a - b) < eps
    }

    // --- 1. Basic case: two obvious clusters ---
    @Test
    fun twoClusters_shouldSeparateCorrectly() {
        // Two dark points, two bright points
        val points = arrayOf(
            LabPoint(10.0, 0.0, 0.0),
            LabPoint(12.0, 1.0, 1.0),
            LabPoint(80.0, 0.0, 0.0),
            LabPoint(82.0, 2.0, 1.0)
        )

        val result = KMeans().apply(points, 2)

        // We should get exactly 2 centers
        assertEquals(2, result.size)

        // Sort by lightness (L) to compare dark and bright
        val sorted = result.sortedBy { it.l }

        val darkCenter = sorted[0]
        val brightCenter = sorted[1]

        // Dark cluster mean: L = (10+12)/2 = 11, A = 0.5, B = 0.5
        assertTrue(almostEqual(darkCenter.l, 11.0))
        assertTrue(almostEqual(darkCenter.a, 0.5))
        assertTrue(almostEqual(darkCenter.b, 0.5))

        // Bright cluster mean: L = (80+82)/2 = 81, A = 1.0, B = 0.5
        assertTrue(almostEqual(brightCenter.l, 81.0))
        assertTrue(almostEqual(brightCenter.a, 1.0))
        assertTrue(almostEqual(brightCenter.b, 0.5))
    }


    // --- 2. All points are identical ---
    @Test
    fun identicalPoints_shouldReturnOneCenter() {
        // Even if we ask for 3 clusters, all points are the same
        // The algorithm can only find 1 unique center
        val points = Array(10) {
            LabPoint(50.0, 10.0, 5.0)
        }

        val result = KMeans().apply(points, 3)

        // We expect exactly 1 center because all points are identical
        // (the loop stops when total distance = 0)
        assertEquals(1, result.size)

        val center = result[0]
        assertTrue(almostEqual(center.l, 50.0))
        assertTrue(almostEqual(center.a, 10.0))
        assertTrue(almostEqual(center.b, 5.0))
    }

    // --- 3. More clusters than points ---
    @Test
    fun moreClustersThanPoints_shouldNotCrash() {
        val points = arrayOf(
            LabPoint(10.0, 0.0, 0.0),
            LabPoint(20.0, 0.0, 0.0)
        )

        val result = KMeans().apply(points, 5)

        // Algorithm should return at most number of unique points (2)
        assertNotNull(result)
        assertTrue(result.size <= points.size)
    }

    // --- 4. Different runs may give different results (randomness) ---
    // This test shows that results are not always the same.
    // We run twice and check that they can be different.
    @Test
    fun differentRuns_mayGiveDifferentResults() {
        val points = arrayOf(
            LabPoint(10.0, 0.0, 0.0),
            LabPoint(12.0, 1.0, 1.0),
            LabPoint(80.0, 0.0, 0.0),
            LabPoint(82.0, 2.0, 1.0)
        )

        val kMeans = KMeans()
        val result1 = kMeans.apply(points, 2)
        val result2 = kMeans.apply(points, 2)

        // Because of randomness, results may be different.
        // We just check they both have 2 centers.
        assertEquals(2, result1.size)
        assertEquals(2, result2.size)

        // Optional: print to see difference
        println("Run 1: $result1")
        println("Run 2: $result2")
    }

    // --- 5. Points are already good centers ---
    @Test
    fun pointsAlreadyCenters_shouldStay() {
        val points = arrayOf(
            LabPoint(0.0, 0.0, 0.0),
            LabPoint(100.0, 0.0, 0.0)
        )

        val result = KMeans().apply(points, 2)

        val sorted = result.sortedBy { it.l }
        assertTrue(almostEqual(sorted[0].l, 0.0))
        assertTrue(almostEqual(sorted[1].l, 100.0))
    }

    // --- 6. Unbalanced clusters ---
    @Test
    fun unbalancedClusters_shouldStillFindCenters() {
        val points = mutableListOf<LabPoint>()
        // 50 dark points (L from 10 to 15)
        repeat(50) { i ->
            points.add(LabPoint(10.0 + i * 0.1, 0.0, 0.0))
        }
        // 3 bright points (L = 90)
        repeat(3) {
            points.add(LabPoint(90.0, 0.0, 0.0))
        }

        val result = KMeans().apply(points.toTypedArray(), 2)

        // One cluster should be dark (L < 30), another bright (L > 70)
        val hasDark = result.any { it.l < 30 }
        val hasBright = result.any { it.l > 70 }
        assertTrue(hasDark)
        assertTrue(hasBright)
    }

    // --- 7. Empty input ---
    @Test
    fun emptyInput_shouldReturnEmptyList() {
        val points = emptyArray<LabPoint>()

        try {
            val result = KMeans().apply(points, 2)
            assertTrue(result.isEmpty())
        } catch (e: Exception) {
            fail("Should not throw exception for empty input")
        }
    }
}