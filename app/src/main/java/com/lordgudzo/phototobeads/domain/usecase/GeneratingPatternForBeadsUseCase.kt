package com.lordgudzo.phototobeads.domain.usecase

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.graphics.ColorUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random
import kotlin.math.min

/**
 * UseCase: Generate cross-stitch pattern from a bitmap
 */
class GeneratingPatternForBeadsUseCase(
    private val fullThreadPalette: List<ThreadColor> // All available thread colors
) {

    /**
     * Main function to generate pattern
     */
    suspend fun execute(
        inputBitmap: Bitmap,
        gridSize: Int,
        colorCount: Int,
        ditherStrength: Float = 0.7f,
        smoothing: Int = 1
    ): PatternResult = withContext(Dispatchers.Default) {

        // ======== 0. Validate input ========
        require(gridSize in 50..500) { "gridSize must be 50..500" }
        require(colorCount in 2..100) { "colorCount must be 2..100" }
        require(ditherStrength in 0f..1f) { "ditherStrength must be 0..1" }
        require(fullThreadPalette.isNotEmpty()) { "Thread palette cannot be empty" }

        // ======== 1. Resize ========
        val resized = progressiveResize(inputBitmap, gridSize)

        // ======== 2. Smooth ========
        val smoothed = if (smoothing > 0) perChannelMedianFilter(resized, smoothing) else resized

        // ======== 3. Convert to LAB ========
        val labPixels = extractLabPixels(smoothed)

        // ======== 4. Palette in LAB ========
        val fullPaletteLab = fullThreadPalette.map { it.toLabPoint() }

        // ======== 5. K-means++ ========
        val sampleSize = min(5000, labPixels.size)
        val sampledIndices = List(sampleSize) { Random.nextInt(labPixels.size) }.distinct()
        val sampledPoints = sampledIndices.map { labPixels[it] }
        val centers = kMeansQuantize(sampledPoints, colorCount)

        // ======== 6. Pick closest threads ========
        var selectedThreads = selectThreadSubset(centers, fullThreadPalette, fullPaletteLab)

        // ======== 7. Ensure at least 2 colors ========
        if (selectedThreads.size < 2) selectedThreads = fullThreadPalette.take(2)

        // ======== 8. Prepare for dithering ========
        val selectedColorsLab = selectedThreads.map { it.toLabPoint() }
        val selectedIndices = selectedThreads.indices.toList()

        // ======== 9. Floyd-Steinberg dithering ========
        val ditheredIndices = floydSteinbergSerpentine(
            labPixels, selectedColorsLab, selectedIndices, gridSize, gridSize, ditherStrength
        )

        // ======== 10. Majority filter ========
        val cleanedIndices = fastMajorityFilter(ditheredIndices, gridSize, gridSize, selectedThreads.size)

        // ======== 11. Build result ========
        buildPatternResult(cleanedIndices, gridSize, selectedThreads)
    }

    // ==================== PRIVATE HELPERS ====================

    /** Progressive resize to target size */
    private fun progressiveResize(bitmap: Bitmap, targetSize: Int): Bitmap {
        var width = bitmap.width
        var height = bitmap.height
        var current = bitmap
        while (width > targetSize * 2 && height > targetSize * 2) {
            val newWidth = width / 2
            val newHeight = height / 2
            current = resizeBicubic(current, newWidth, newHeight)
            width = newWidth
            height = newHeight
        }
        return resizeBicubic(current, targetSize, targetSize)
    }

    /** Bicubic resize using Canvas */
    private fun resizeBicubic(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val scaled = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(scaled)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        paint.isFilterBitmap = true
        canvas.drawBitmap(bitmap, null, Rect(0, 0, newWidth, newHeight), paint)
        return scaled
    }

    /** Median filter per channel 3x3 */
    private fun perChannelMedianFilter(bitmap: Bitmap, level: Int): Bitmap {
        val result = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        repeat(level) {
            val temp = pixels.clone()
            for (y in 1 until height - 1) {
                for (x in 1 until width - 1) {
                    val reds = mutableListOf<Int>()
                    val greens = mutableListOf<Int>()
                    val blues = mutableListOf<Int>()
                    for (dy in -1..1) for (dx in -1..1) {
                        val c = temp[(y + dy) * width + (x + dx)]
                        reds.add(Color.red(c))
                        greens.add(Color.green(c))
                        blues.add(Color.blue(c))
                    }
                    reds.sort(); greens.sort(); blues.sort()
                    pixels[y * width + x] = Color.rgb(reds[4], greens[4], blues[4])
                }
            }
        }
        result.setPixels(pixels, 0, width, 0, 0, width, height)
        return result
    }

    /** Convert Bitmap to LAB array */
    private fun extractLabPixels(bitmap: Bitmap): Array<LabPoint> {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        return Array(pixels.size) { i ->
            val color = pixels[i]
            val lab = DoubleArray(3)
            ColorUtils.RGBToLAB(Color.red(color), Color.green(color), Color.blue(color), lab)
            LabPoint(lab[0], lab[1], lab[2])
        }
    }

    /** K-means++ clustering */
    private fun kMeansQuantize(points: List<LabPoint>, k: Int): List<LabPoint> {
        if (points.isEmpty() || k == 0) return emptyList()
        val random = Random(System.currentTimeMillis())
        val centers = mutableListOf(points[random.nextInt(points.size)])
        while (centers.size < k) {
            val distances = points.map { pt -> centers.minOf { it.squaredDistance(pt) } }
            val total = distances.sum()
            if (total == 0.0) break
            var r = random.nextDouble() * total
            var idx = 0
            while (r > 0 && idx < distances.size) { r -= distances[idx]; idx++ }
            centers.add(points[(idx - 1).coerceIn(points.indices)])
        }

        val assignments = IntArray(points.size)
        var changed = true
        var iteration = 0
        val maxIterations = 20
        while (changed && iteration < maxIterations) {
            changed = false
            for (i in points.indices) {
                val bestIdx = centers.indices.minByOrNull { points[i].squaredDistance(centers[it]) }!!
                if (assignments[i] != bestIdx) { assignments[i] = bestIdx; changed = true }
            }
            if (!changed) break
            val sums = Array(centers.size) { Triple(0.0, 0.0, 0.0) }
            val counts = IntArray(centers.size)
            for (i in points.indices) {
                val idx = assignments[i]
                val p = points[i]
                sums[idx] = Triple(sums[idx].first + p.l, sums[idx].second + p.a, sums[idx].third + p.b)
                counts[idx]++
            }
            for (i in centers.indices) {
                centers[i] = if (counts[i] > 0) LabPoint(sums[i].first / counts[i], sums[i].second / counts[i], sums[i].third / counts[i])
                else points[random.nextInt(points.size)]
            }
            iteration++
        }
        return centers
    }

    /** Select closest threads from palette */
    private fun selectThreadSubset(
        centers: List<LabPoint>,
        fullPalette: List<ThreadColor>,
        fullPaletteLab: List<LabPoint>
    ): List<ThreadColor> {
        val selected = mutableSetOf<ThreadColor>()
        for (center in centers) {
            var bestIdx = 0
            var bestDist = Double.POSITIVE_INFINITY
            for (i in fullPaletteLab.indices) {
                val dist = center.squaredDistance(fullPaletteLab[i])
                if (dist < bestDist) { bestDist = dist; bestIdx = i }
            }
            selected.add(fullPalette[bestIdx])
        }
        return selected.toList()
    }

    /** Floyd-Steinberg dithering serpentine */
    private fun floydSteinbergSerpentine(
        labPixels: Array<LabPoint>,
        targetColorsLab: List<LabPoint>,
        targetIndices: List<Int>,
        width: Int,
        height: Int,
        strength: Float
    ): IntArray {
        val labBuffer = Array(labPixels.size) { labPixels[it].copy() }
        val result = IntArray(labPixels.size)
        for (y in 0 until height) {
            val range = if (y % 2 == 0) 0 until width else width - 1 downTo 0
            for (x in range) {
                val idx = y * width + x
                val current = labBuffer[idx]
                var bestIdx = 0
                var bestDist = Double.POSITIVE_INFINITY
                for (i in targetColorsLab.indices) {
                    val dist = current.squaredDistance(targetColorsLab[i])
                    if (dist < bestDist) { bestDist = dist; bestIdx = i }
                }
                val targetLab = targetColorsLab[bestIdx]
                result[idx] = targetIndices[bestIdx]
                val errorL = current.l - targetLab.l
                val errorA = current.a - targetLab.a
                val errorB = current.b - targetLab.b
                fun distribute(dx: Int, dy: Int, factor: Float) {
                    val nx = x + dx * if (y % 2 == 0) 1 else -1
                    val ny = y + dy
                    if (nx in 0 until width && ny in 0 until height) {
                        val nIdx = ny * width + nx
                        labBuffer[nIdx].l += errorL * factor * strength
                        labBuffer[nIdx].a += errorA * factor * strength
                        labBuffer[nIdx].b += errorB * factor * strength
                        labBuffer[nIdx].clamp()
                    }
                }
                distribute(1, 0, 7f / 16f)
                distribute(-1, 1, 3f / 16f)
                distribute(0, 1, 5f / 16f)
                distribute(1, 1, 1f / 16f)
            }
        }
        return result
    }

    /** Majority filter 3x3 */
    private fun fastMajorityFilter(indices: IntArray, width: Int, height: Int, paletteSize: Int): IntArray {
        val result = indices.copyOf()
        val counts = IntArray(paletteSize)
        for (y in 1 until height - 1) {
            for (x in 1 until width - 1) {
                val centerIdx = y * width + x
                counts.fill(0)
                for (dy in -1..1) for (dx in -1..1) counts[indices[(y + dy) * width + x + dx]]++
                var dominantColor = indices[centerIdx]
                var maxCount = counts[dominantColor]
                for (c in 0 until paletteSize) if (counts[c] > maxCount) { dominantColor = c; maxCount = counts[c] }
                if (dominantColor != indices[centerIdx] && maxCount >= 5) result[centerIdx] = dominantColor
            }
        }
        return result
    }

    /** Build PatternResult with symbols */
    private fun buildPatternResult(indices: IntArray, size: Int, selectedThreads: List<ThreadColor>): PatternResult {
        val symbolMap = generateDistinctSymbols(selectedThreads.size)
        return PatternResult(
            width = size,
            height = size,
            indices = indices,
            palette = selectedThreads,
            symbolMap = selectedThreads.indices.associateWith { symbolMap[it] }
        )
    }

    /** Generate distinct symbols for threads */
    private fun generateDistinctSymbols(count: Int): List<String> {
        val base = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        val extras = listOf("■","□","▲","△","●","○","♥","♦","♣","♠","★","☆","☀","☁","☂")
        val all = base.map { it.toString() } + extras
        val result = mutableListOf<String>()
        result.addAll(all.take(count))
        while (result.size < count) result.add("X${result.size}")
        return result
    }
}

// ==================== DATA CLASSES ====================
data class LabPoint(var l: Double, var a: Double, var b: Double) {
    fun squaredDistance(other: LabPoint) = (l - other.l).let { dl -> dl * dl } +
            (a - other.a).let { da -> da * da } +
            (b - other.b).let { db -> db * db }
    fun copy() = LabPoint(l, a, b)
    fun clamp() { l = l.coerceIn(0.0, 100.0); a = a.coerceIn(-128.0, 127.0); b = b.coerceIn(-128.0, 127.0) }
}

data class ThreadColor(val code: String, val colorInt: Int) {
    fun toLabPoint(): LabPoint {
        val lab = DoubleArray(3)
        ColorUtils.RGBToLAB(Color.red(colorInt), Color.green(colorInt), Color.blue(colorInt), lab)
        return LabPoint(lab[0], lab[1], lab[2])
    }
}

data class PatternResult(
    val width: Int,
    val height: Int,
    val indices: IntArray,
    val palette: List<ThreadColor>,
    val symbolMap: Map<Int, String>
)