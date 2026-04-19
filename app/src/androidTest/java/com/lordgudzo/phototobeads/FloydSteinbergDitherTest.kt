package com.lordgudzo.phototobeads

import com.lordgudzo.phototobeads.domain.imageprocessing.FloydSteinbergDither
import com.lordgudzo.phototobeads.domain.model.BeadColor
import com.lordgudzo.phototobeads.domain.model.LabPoint
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

class FloydSteinbergDitherTest {
    private fun bead(code: String, r: Int, g: Int, b: Int) =
        BeadColor(code, "", r, g, b)

    private fun lab(l: Double, a: Double = 0.0, b: Double = 0.0) =
        LabPoint(l, a, b)

    // ---------- BASIC ----------

    @Test
    fun debugSmallGradientWithThreeColors() {
        val width = 10
        val height = 1

        val pixels = Array(width) { i ->
            LabPoint(i * 10.0, 0.0, 0.0) // L: 0 → 90
        }

        val palette = listOf(
            BeadColor("Black", "", 0, 0, 0),
            BeadColor("Gray", "", 128, 128, 128),
            BeadColor("White", "", 255, 255, 255)
        )

        val dither = FloydSteinbergDither()

        val result = dither.apply(
            pixels,
            palette,
            width,
            height,
            strength = 1f
        )

        // ---- DEBUG OUTPUT ----
        println("=== DEBUG SMALL DITHER ===")
        println("Input L values:")
        println(pixels.map { it.l })

        println("Result indices:")
        println(result.toList())

        println("Result colors:")
        println(result.map { palette[it].code })

        // ---- ASSERTIONS ----


        assertEquals(width * height, result.size)


        assertTrue(result.all { it in palette.indices })


        assertTrue(result.toSet().size >= 2)

    }

    @Test
    fun returnsCorrectSize() {
        val width = 4
        val height = 3

        val pixels = Array(width * height) { lab(50.0) }
        val palette = listOf(
            bead("A", 0, 0, 0),
            bead("B", 255, 255, 255)
        )

        val result = FloydSteinbergDither().apply(pixels, palette, width, height, 1f)

        assertEquals(width * height, result.size)
    }

    @Test
    fun indicesAlwaysWithinPaletteBounds() {
        val pixels = Array(100) { lab(50.0) }
        val palette = listOf(
            bead("A", 0, 0, 0),
            bead("B", 255, 255, 255)
        )

        val result = FloydSteinbergDither().apply(pixels, palette, 10, 10, 1f)

        assertTrue(result.all { it in palette.indices })
    }

    // ---------- NO DITHER ----------

    @Test
    fun strengthZeroBehavesLikeNearestColor() {
        val pixels = arrayOf(
            lab(10.0),
            lab(90.0)
        )

        val palette = listOf(
            bead("Dark", 0, 0, 0),
            bead("Light", 255, 255, 255)
        )

        val result = FloydSteinbergDither().apply(pixels, palette, 2, 1, 0f)

        // No error diffusion → pure nearest match
        assertEquals(0, result[0]) // dark
        assertEquals(1, result[1]) // light
    }


    // ---------- DITHER EFFECT ----------

    @Test
    fun gradientProducesMultipleColorsWithDither() {
        val width = 10
        val height = 1

        val pixels = Array(width) { i ->
            lab(i * 10.0) // gradient 0..90
        }

        val palette = listOf(
            bead("Black", 0, 0, 0),
            bead("White", 255, 255, 255)
        )

        val result = FloydSteinbergDither().apply(pixels, palette, width, height, 1f)

        // With dithering → must contain both colors
        assertTrue(result.toSet().size > 1)
    }

    // ---------- SERPENTINE ----------

    @Test
    fun serpentineDoesNotCrashAndProcessesAllPixels() {
        val width = 7
        val height = 7

        val pixels = Array(width * height) { lab((it % 100).toDouble()) }

        val palette = listOf(
            bead("A", 0, 0, 0),
            bead("B", 255, 255, 255)
        )

        val result = FloydSteinbergDither().apply(pixels, palette, width, height, 1f)

        assertEquals(width * height, result.size)
    }

    // ---------- STABILITY ----------

    @Test
    fun identicalInputProducesDeterministicOutput() {
        val pixels = Array(50) { lab(42.0) }
        val palette = listOf(
            bead("A", 0, 0, 0),
            bead("B", 255, 255, 255)
        )

        val dither = FloydSteinbergDither()

        val result1 = dither.apply(pixels, palette, 10, 5, 1f)
        val result2 = dither.apply(pixels, palette, 10, 5, 1f)

        assertEquals(result1.toList(), result2.toList())
    }

    // ---------- EDGE CASE ----------

    @Test
    fun singlePixelImageWorks() {
        val pixels = arrayOf(lab(50.0))
        val palette = listOf(
            bead("A", 0, 0, 0),
            bead("B", 255, 255, 255)
        )

        val result = FloydSteinbergDither().apply(pixels, palette, 1, 1, 1f)

        assertEquals(1, result.size)
        assertTrue(result[0] in palette.indices)
    }
}