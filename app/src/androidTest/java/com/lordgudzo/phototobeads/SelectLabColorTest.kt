package com.lordgudzo.phototobeads

import com.lordgudzo.phototobeads.domain.imageprocessing.SelectLabColor
import com.lordgudzo.phototobeads.domain.model.BeadColor
import com.lordgudzo.phototobeads.domain.model.LabPoint
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test
import kotlin.math.sqrt

class SelectLabColorTest {

    // --- helpers ---

    private fun beadColor(code: String, r: Int, g: Int, b: Int) =
        BeadColor(code, "", r, g, b)

    private fun lab(l: Double, a: Double, b: Double) =
        LabPoint(l, a, b)

    private fun distance(a: LabPoint, b: LabPoint): Double {
        return sqrt(
            (a.l - b.l) * (a.l - b.l) +
                    (a.a - b.a) * (a.a - b.a) +
                    (a.b - b.b) * (a.b - b.b)
        )
    }

    // --- base tests (твои) ---

    @Test
    fun returnsEmptyListWhenCentersEmpty() {
        val centers = emptyList<LabPoint>()
        val palette = listOf(beadColor("A", 0, 0, 0))
        val paletteLab = palette.map { it.toLabPoint() }

        val result = SelectLabColor().apply(centers, palette, paletteLab)

        assertTrue(result.isEmpty())
    }

    @Test
    fun returnsUniqueColorsWhenCentersEqualsPaletteSize() {
        val centers = listOf(
            lab(10.0, 0.0, 0.0),
            lab(20.0, 0.0, 0.0),
            lab(30.0, 0.0, 0.0)
        )
        val palette = listOf(
            beadColor("Black", 0, 0, 0),
            beadColor("White", 255, 255, 255),
            beadColor("Gray", 128, 128, 128)
        )
        val paletteLab = palette.map { it.toLabPoint() }

        val result = SelectLabColor().apply(centers, palette, paletteLab)

        assertEquals(centers.size, result.size)
        assertEquals(result.size, result.distinct().size)
        assertTrue(result.all { it in palette })
    }

    @Test
    fun returnsUniqueSubsetWhenCentersLessThanPalette() {
        val centers = listOf(
            lab(10.0, 0.0, 0.0),
            lab(90.0, 0.0, 0.0)
        )
        val palette = listOf(
            beadColor("Dark", 20, 0, 0),
            beadColor("Medium", 128, 0, 0),
            beadColor("Light", 200, 0, 0)
        )
        val paletteLab = palette.map { it.toLabPoint() }

        val result = SelectLabColor().apply(centers, palette, paletteLab)

        assertEquals(2, result.size)
        assertEquals(2, result.distinct().size)
    }

    @Test
    fun allowsDuplicatesWhenCentersGreaterThanPalette() {
        val centers = listOf(
            lab(10.0, 0.0, 0.0),
            lab(20.0, 0.0, 0.0),
            lab(30.0, 0.0, 0.0)
        )
        val palette = listOf(
            beadColor("Black", 0, 0, 0),
            beadColor("White", 255, 255, 255)
        )
        val paletteLab = palette.map { it.toLabPoint() }

        val result = SelectLabColor().apply(centers, palette, paletteLab)

        assertEquals(centers.size, result.size)
        assertTrue(result.all { it in palette })
    }

    @Test
    fun assignsNearestDistinctColorsForSimpleCase() {
        val centers = listOf(
            lab(15.0, 0.0, 0.0),
            lab(85.0, 0.0, 0.0)
        )
        val palette = listOf(
            beadColor("Dark", 10, 0, 0),
            beadColor("Mid", 128, 128, 128),
            beadColor("Light", 240, 240, 240)
        )
        val paletteLab = listOf(
            lab(10.0, 0.0, 0.0),
            lab(50.0, 0.0, 0.0),
            lab(90.0, 0.0, 0.0)
        )

        val result = SelectLabColor().apply(centers, palette, paletteLab)

        val codes = result.map { it.code }.toSet()
        assertEquals(setOf("Dark", "Light"), codes)
    }

    @Test
    fun selectsNearestColorByEuclideanDistance() {
        val center = lab(0.0, 0.0, 0.0)

        val palette = listOf(
            beadColor("Near", 0, 0, 0),
            beadColor("Far", 0, 0, 0)
        )
        val paletteLab = listOf(
            lab(10.0, 0.0, 0.0),
            lab(20.0, 0.0, 0.0)
        )

        val result = SelectLabColor().apply(listOf(center), palette, paletteLab)

        assertEquals("Near", result.first().code)
    }

    @Test
    fun returnsValidPaletteColorsForArbitraryInput() {
        val palette = (0..9).map {
            beadColor("C$it", it * 25, it * 25, it * 25)
        }
        val paletteLab = palette.map { it.toLabPoint() }

        val centers = (0..4).map {
            lab(it * 20.0, 0.0, 0.0)
        }

        val result = SelectLabColor().apply(centers, palette, paletteLab)

        assertEquals(centers.size, result.size)
        assertTrue(result.all { it in palette })
    }

    // --- дополнительные (ключевые) ---

    @Test
    fun paletteAndLabAreConsistentByIndex() {
        val palette = listOf(
            beadColor("A", 10, 0, 0),
            beadColor("B", 128, 0, 0),
            beadColor("C", 240, 0, 0)
        )
        val paletteLab = palette.map { it.toLabPoint() }

        palette.forEachIndexed { i, color ->
            val expected = color.toLabPoint()
            val actual = paletteLab[i]
            val dist = distance(expected, actual)

            assertTrue("Mismatch at index $i: $dist", dist < 0.0001)
        }
    }

    @Test
    fun isDeterministic() {
        val centers = listOf(
            lab(10.0, 0.0, 0.0),
            lab(50.0, 0.0, 0.0),
            lab(90.0, 0.0, 0.0)
        )
        val palette = (0..9).map {
            beadColor("C$it", it * 25, 0, 0)
        }
        val paletteLab = palette.map { it.toLabPoint() }

        val selector = SelectLabColor()

        val r1 = selector.apply(centers, palette, paletteLab)
        val r2 = selector.apply(centers, palette, paletteLab)

        assertEquals(r1, r2)
    }

    @Test
    fun handlesExtremeLabValues() {
        val palette = listOf(
            beadColor("Black", 0, 0, 0),
            beadColor("White", 255, 255, 255)
        )
        val paletteLab = palette.map { it.toLabPoint() }

        val centers = listOf(
            lab(-100.0, -200.0, -200.0),
            lab(200.0, 200.0, 200.0)
        )

        val result = SelectLabColor().apply(centers, palette, paletteLab)

        assertEquals(2, result.size)
        assertTrue(result.all { it in palette })
    }

    @Test
    fun identicalCentersProduceSameColor() {
        val palette = listOf(
            beadColor("A", 0, 0, 0),
            beadColor("B", 255, 255, 255)
        )
        val paletteLab = palette.map { it.toLabPoint() }

        val centers = List(5) { lab(10.0, 0.0, 0.0) }

        val result = SelectLabColor().apply(centers, palette, paletteLab)

        assertEquals(1, result.toSet().size)
    }

    @Test
    fun doesNotBiasTowardsFirstPaletteColor() {
        val palette = listOf(
            beadColor("A", 0, 0, 0),
            beadColor("B", 255, 255, 255)
        )
        val paletteLab = listOf(
            lab(0.0, 0.0, 0.0),
            lab(100.0, 0.0, 0.0)
        )

        val centers = listOf(
            lab(90.0, 0.0, 0.0)
        )

        val result = SelectLabColor().apply(centers, palette, paletteLab)

        assertEquals("B", result.first().code)
    }

    @Test
    fun everySelectedColorIsReachable() {
        val palette = (0..19).map {
            beadColor("C$it", it * 10, it * 10, it * 10)
        }
        val paletteLab = palette.map { it.toLabPoint() }

        val centers = paletteLab

        val result = SelectLabColor().apply(centers, palette, paletteLab)

        val expected = palette.map { it.code }.toSet()
        val actual = result.map { it.code }.toSet()

        assertEquals(expected, actual)
    }

    @Test
    fun paletteOrderDoesNotAffectResult() {
        val palette = listOf(
            beadColor("A", 0, 0, 0),
            beadColor("B", 128, 0, 0),
            beadColor("C", 255, 0, 0)
        )
        val paletteLab = palette.map { it.toLabPoint() }

        val centers = listOf(
            lab(10.0, 0.0, 0.0),
            lab(200.0, 0.0, 0.0)
        )

        val result1 = SelectLabColor().apply(centers, palette, paletteLab)

        val shuffled = palette.zip(paletteLab).shuffled()
        val palette2 = shuffled.map { it.first }
        val paletteLab2 = shuffled.map { it.second }

        val result2 = SelectLabColor().apply(centers, palette2, paletteLab2)

        assertEquals(
            result1.map { it.code }.toSet(),
            result2.map { it.code }.toSet()
        )
    }
}