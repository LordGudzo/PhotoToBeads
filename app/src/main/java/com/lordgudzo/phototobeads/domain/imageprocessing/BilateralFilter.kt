package com.lordgudzo.phototobeads.domain.imageprocessing

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.exp

/**
 * Bilateral filter — smooths image but keeps edges sharp.
 *
 * How it works:
 * For each pixel we look at neighbors in radius.
 * Each neighbor gets a weight based on two things:
 * 1. Space distance — far neighbors matter less (sigmaSpace)
 * 2. Color distance — neighbors with very different color matter less (sigmaColor)
 *
 * Result: noise removed, edges stay sharp.
 *
 * @param sigmaSpace — how far we look (radius). Bigger = more blur. Good value: 2.0
 * @param sigmaColor — how different colors can be. Bigger = less edge protection. Good value: 25.0
 */
class BilateralFilter {

    fun apply(bitmap: Bitmap, sigmaSpace: Double = 2.0, sigmaColor: Double = 25.0): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val result = IntArray(width * height)

        // Radius — how many pixels around center we check
        val radius = (sigmaSpace * 2).toInt().coerceAtLeast(1)

        // Pre-calculate space weights — they don't change per pixel
        // spaceWeights[dy + radius][dx + radius] = weight for that offset
        val size = 2 * radius + 1
        val spaceWeights = Array(size) { dy ->
            DoubleArray(size) { dx ->
                val distSq = (dx - radius) * (dx - radius) + (dy - radius) * (dy - radius).toDouble()
                exp(-distSq / (2.0 * sigmaSpace * sigmaSpace))
            }
        }

        for (y in 0 until height) {
            for (x in 0 until width) {

                val centerColor = pixels[y * width + x]
                val centerR = Color.red(centerColor)
                val centerG = Color.green(centerColor)
                val centerB = Color.blue(centerColor)

                var sumR = 0.0
                var sumG = 0.0
                var sumB = 0.0
                var sumWeight = 0.0

                for (dy in -radius..radius) {
                    for (dx in -radius..radius) {
                        val nx = (x + dx).coerceIn(0, width - 1)
                        val ny = (y + dy).coerceIn(0, height - 1)

                        val neighborColor = pixels[ny * width + nx]
                        val neighborR = Color.red(neighborColor)
                        val neighborG = Color.green(neighborColor)
                        val neighborB = Color.blue(neighborColor)

                        // Color distance (how similar colors are)
                        val colorDiffSq =
                            (centerR - neighborR) * (centerR - neighborR) +
                                    (centerG - neighborG) * (centerG - neighborG) +
                                    (centerB - neighborB) * (centerB - neighborB).toDouble()

                        val colorWeight = exp(-colorDiffSq / (2.0 * sigmaColor * sigmaColor))

                        // Final weight = space weight * color weight
                        val weight = spaceWeights[dy + radius][dx + radius] * colorWeight

                        sumR += neighborR * weight
                        sumG += neighborG * weight
                        sumB += neighborB * weight
                        sumWeight += weight
                    }
                }

                result[y * width + x] = Color.rgb(
                    (sumR / sumWeight).toInt().coerceIn(0, 255),
                    (sumG / sumWeight).toInt().coerceIn(0, 255),
                    (sumB / sumWeight).toInt().coerceIn(0, 255)
                )
            }
        }

        val output = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        output.setPixels(result, 0, width, 0, 0, width, height)
        return output
    }
}

