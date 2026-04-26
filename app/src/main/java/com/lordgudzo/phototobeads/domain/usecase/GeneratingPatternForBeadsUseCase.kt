package com.lordgudzo.phototobeads.domain.usecase

import android.graphics.Bitmap
import com.lordgudzo.phototobeads.domain.imageprocessing.BilateralFilter
import com.lordgudzo.phototobeads.domain.imageprocessing.BitMapToLabArray
import com.lordgudzo.phototobeads.domain.imageprocessing.BuildResult
import com.lordgudzo.phototobeads.domain.imageprocessing.FastMajorityFilter
import com.lordgudzo.phototobeads.domain.imageprocessing.FloydSteinbergDither
import com.lordgudzo.phototobeads.domain.imageprocessing.KMeans
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
        gridSize: Int,
        colorCount: Int,
        ditherStrength: Float = 0.7f,
        palette: String = "Preciosa",
    ): PatternResult              // = withContext(Dispatchers.Default)
    {

        // ======== 0. Validate input ========
        require(gridSize in 50..500) { "gridSize must be 50..500" }
        require(colorCount in 2..100) { "colorCount must be 2..100" }
        require(ditherStrength in 0f..1f) { "ditherStrength must be 0..1" }

        // ======== 1. Resize ======== Image resize as square with width*height gridSize*gridSize
        val reSizedBitMap: Bitmap = createRepository.getBitMapWithSize(gridSize)

        // ======== 2. Smooth ========
        val smoothed: Bitmap = BilateralFilter().apply(
            bitmap = reSizedBitMap,
            sigmaSpace = 2.0,   // radius of blur
            sigmaColor = 25.0   // edge sensitivity — lower = sharper edges preserved
        )

        // ======== 3. Convert to LAB ========
        val labPixels: Array<LabPoint> = BitMapToLabArray().apply(smoothed)

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
            FastMajorityFilter().apply(ditheredIndices, gridSize, gridSize, selectedColors.size)
        // ======== 10. Build result ========
        return BuildResult().apply(cleanedIndices, gridSize, selectedColors)
    }
}




