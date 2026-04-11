package com.lordgudzo.phototobeads.domain.usecase

import android.graphics.Bitmap
import com.lordgudzo.phototobeads.domain.imageprocessing.BitMapToLabArray
import com.lordgudzo.phototobeads.domain.imageprocessing.KMeans
import com.lordgudzo.phototobeads.domain.imageprocessing.MedianFilter
import com.lordgudzo.phototobeads.domain.imageprocessing.SelectLabColor
import com.lordgudzo.phototobeads.domain.model.BeadColor
import com.lordgudzo.phototobeads.domain.model.LabPoint
import com.lordgudzo.phototobeads.domain.repository.CreatePatternRepository
import kotlin.math.min
import kotlin.random.Random

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
            MedianFilter().apply(reSizedBitMap, smoothing)
        } else reSizedBitMap

        // ======== 3. Convert to LAB ========
        val labPixels: Array<LabPoint> = BitMapToLabArray().apply(smoothed)

        // ======== 4. Palette in LAB ========
        val selectedPalette: List<BeadColor> = createRepository.getPalette(palette)
        val selectedPaletteToLab: List<LabPoint> = selectedPalette.map { it.toLabPoint() }

        // ======== 5. K-means++ ========
        val colorCenters: List<LabPoint> = KMeans().apply(labPixels, colorCount)

        // ======== 6.Choose nearest colors ========
        val selectedColors = SelectLabColor().apply(
            colorCenters,
            selectedPalette,
            selectedPaletteToLab
        )


        val result: Bitmap = smoothed
        return result

    }


}




