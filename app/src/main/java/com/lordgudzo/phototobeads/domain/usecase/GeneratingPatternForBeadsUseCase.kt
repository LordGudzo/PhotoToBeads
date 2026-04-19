package com.lordgudzo.phototobeads.domain.usecase

import android.graphics.Bitmap
import android.util.Log
import com.lordgudzo.phototobeads.domain.imageprocessing.BitMapToLabArray
import com.lordgudzo.phototobeads.domain.imageprocessing.FloydSteinbergDither
import com.lordgudzo.phototobeads.domain.imageprocessing.KMeans
import com.lordgudzo.phototobeads.domain.imageprocessing.MedianFilter
import com.lordgudzo.phototobeads.domain.imageprocessing.SelectLabColor
import com.lordgudzo.phototobeads.domain.model.BeadColor
import com.lordgudzo.phototobeads.domain.model.LabPoint
import com.lordgudzo.phototobeads.domain.model.PatternResult
import com.lordgudzo.phototobeads.domain.repository.CreatePatternRepository

class GeneratingPatternForBeadsUseCase(
    private val createRepository: CreatePatternRepository
) {
    // suspend
    fun execute(
        //   inputBitmap: Bitmap,
        gridSize: Int,
        colorCount: Int,
        ditherStrength: Float = 0.7f,
        palette: String = "Preciosa",
        //smoothing: how many times we apply the Median filter check step 2 MedianFilter() description
        smoothing: Int = 1
    ): Bitmap                                   //: PatternResult = withContext(Dispatchers.Default)
    {

        // ======== 0. Validate input ========
        require(gridSize in 50..500) { "gridSize must be 50..500" }
        require(colorCount in 2..100) { "colorCount must be 2..100" }
        require(ditherStrength in 0f..1f) { "ditherStrength must be 0..1" }
//        require(fullThreadPalette.isNotEmpty()) { "Thread palette cannot be empty" }

        // ======== 1. Resize ======== Image resize as square with width*height gridSize*gridSize
        val reSizedBitMap: Bitmap = createRepository.getBitMapWithSize(gridSize)

        // ======== 2. Smooth ========

        val smoothed: Bitmap = if (smoothing > 0) {
            val filtered = MedianFilter().apply(reSizedBitMap, smoothing)
            // Принудительно возвращаем к нужному размеру
            Bitmap.createScaledBitmap(filtered, gridSize, gridSize, true)
        } else reSizedBitMap

        // ======== 3. Convert to LAB ========
        val labPixels: Array<LabPoint> = BitMapToLabArray().apply(smoothed)

        Log.d("DEBUG", "labPixels.size=${labPixels.size}, expected=${gridSize * gridSize}")
        Log.d("DEBUG", "smoothed: ${smoothed.width}x${smoothed.height}")

        // ======== 4. Palette in LAB ========
        val selectedPalette: List<BeadColor> = createRepository.getPalette(palette)
        val selectedPaletteToLab: List<LabPoint> = selectedPalette.map { it.toLabPoint() }

        // ======== 5. K-means++ ========
        val colorCenters: List<LabPoint> = KMeans().apply(labPixels, colorCount)

        // ======== 6.Choose nearest colors ========
        //I returned value as BeadColor because I need information as name and code.
        val selectedColors: List<BeadColor> = SelectLabColor().apply(
            colorCenters,
            selectedPalette,
            selectedPaletteToLab
        )


        // ======== 7. Floyd-Steinberg dithering ========
        val ditheredIndices: IntArray = FloydSteinbergDither().apply(
            labPixels,
            selectedColors,
            gridSize,
            gridSize,
            ditherStrength
        )

        // ======== 9. Majority filter ========
        val cleanedIndices =
            fastMajorityFilter(ditheredIndices, gridSize, gridSize, selectedColors.size)
        // ======== 10. Build result ========


        val test = buildPatternResult(ditheredIndices, gridSize, selectedColors)


        //   val result: Bitmap = render(test)
        val debugBitmap = renderFromLab(
            labPixels,
            selectedColors.map { it.toLabPoint() },
            selectedColors
        )

        return render(test)

    }


    /** Majority filter 3x3 */
    private fun fastMajorityFilter(
        indices: IntArray,
        width: Int,
        height: Int,
        paletteSize: Int
    ): IntArray {
        val result = indices.copyOf()
        val counts = IntArray(paletteSize)
        for (y in 1 until height - 1) {
            for (x in 1 until width - 1) {
                val centerIdx = y * width + x
                counts.fill(0)
                for (dy in -1..1) for (dx in -1..1) counts[indices[(y + dy) * width + x + dx]]++
                var dominantColor = indices[centerIdx]
                var maxCount = counts[dominantColor]
                for (c in 0 until paletteSize) if (counts[c] > maxCount) {
                    dominantColor = c; maxCount = counts[c]
                }
                if (dominantColor != indices[centerIdx] && maxCount >= 5) result[centerIdx] =
                    dominantColor
            }
        }
        return result
    }

    /** Build PatternResult with symbols */
    private fun buildPatternResult(
        indices: IntArray,
        size: Int,
        selectedThreads: List<BeadColor>
    ): PatternResult {
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
        val extras =
            listOf("■", "□", "▲", "△", "●", "○", "♥", "♦", "♣", "♠", "★", "☆", "☀", "☁", "☂")
        val all = base.map { it.toString() } + extras
        val result = mutableListOf<String>()
        result.addAll(all.take(count))
        while (result.size < count) result.add("X${result.size}")
        return result
    }

    fun render(result: PatternResult): Bitmap {

        val bitmap = Bitmap.createBitmap(
            result.width,
            result.height,
            Bitmap.Config.ARGB_8888
        )

        for (y in 0 until result.height) {
            for (x in 0 until result.width) {
                val idx = y * result.width + x
                val colorIndex = result.indices[idx]
                val color = result.palette[colorIndex]

                bitmap.setPixel(x, y, color.colorInt)
            }
        }

        return bitmap
    }

    private fun renderFromLab(
        labPixels: Array<LabPoint>,
        paletteLab: List<LabPoint>,
        palette: List<BeadColor>
    ): Bitmap {

        val width = kotlin.math.sqrt(labPixels.size.toDouble()).toInt()
        val height = width

        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (i in labPixels.indices) {
            val p = labPixels[i]

            var bestIdx = 0
            var bestDist = Double.MAX_VALUE

            for (j in paletteLab.indices) {
                val d = p.checkLabPointDistance(paletteLab[j])
                if (d < bestDist) {
                    bestDist = d
                    bestIdx = j
                }
            }

            result.setPixel(
                i % width,
                i / width,
                palette[bestIdx].colorInt
            )
        }

        return result
    }


}




