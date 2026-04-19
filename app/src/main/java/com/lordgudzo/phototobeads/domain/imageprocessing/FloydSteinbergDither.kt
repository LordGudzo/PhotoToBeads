package com.lordgudzo.phototobeads.domain.imageprocessing

import com.lordgudzo.phototobeads.domain.model.BeadColor
import com.lordgudzo.phototobeads.domain.model.LabPoint

/**
 * Imagine you have a gradient:
 * pixel 1 is light gray (L=90), pixel 2 is slightly darker (L=89), pixel 3 is even darker (L=88), and so on.
 * Your palette consists of only two colors: black (L=0) and white (L=100).
 * If you simply replace each pixel with the closest color from the palette:
 * - All gray shades from 50 to 100 will become white.
 * - All shades from 0 to 49 will become black.
 *
 * **The eye will see this as a band.**
 * But if distribute it as the error, a white pixel that was supposed to be light gray will receive
 * a bit of black 'input' from its neighbor, shifting its color slightly toward dark.
 * The next pixel will receive more, and so forth.
 *
 * **In the end, instead of a sharp boundary,
 * you get a fine alternation** of black and white dots (a pattern) that the eye perceives as medium gray.*/
class FloydSteinbergDither {
    /** Floyd-Steinberg dithering serpentine
     *
     * @param labPixels original image (LAB colors)
     * @param selectedColors palette of allowed colors
     * @param strength how strong the error effect is (0=off, 1=full)
     * @return IntArray of size (width * height), where each element is an index of the selectedColors List<BeadColor>
     * */
    fun apply(
        labPixels: Array<LabPoint>,
        selectedColors: List<BeadColor>,
        width: Int,
        height: Int,
        strength: Float
    ): IntArray {
        // palette of allowed colors (LAB)
        val targetColorsLab = selectedColors.map { it.toLabPoint() }
        // indices of these colors (0,1,2,...)
        val targetIndices = selectedColors.indices.toList()


        //<editor-fold desc="Make a copy of the image">
        // We will change this copy when spreading errors.
        val labBuffer = Array(labPixels.size) { labPixels[it].copy() }
        //This array will hold the final index of the chosen color for each pixel.
        val result = IntArray(labPixels.size)
        //</editor-fold>

        //Go through all rows (y = top to bottom)
        for (y in 0 until height) {
            //Serpentine: even rows go left→right, odd rows go right→left.
            val range =
                if (y % 2 == 0) 0 until width   // y % 2 == 0 means even row (0,2,4,...) -> range 0..width-1
                else width - 1 downTo 0         // else (odd row) -> range width-1 down to 0

            //Go through each pixel in the row (x depends on direction)
            for (x in range) {
                val indexOfPixel = y * width + x      //Index of the pixel in the flat array
                val current = labBuffer[indexOfPixel] // Current pixel color
                //Find the nearest color from the palette
                var bestIdx = 0
                var bestDist = Double.POSITIVE_INFINITY
                for (i in targetColorsLab.indices) {
                    val dist = current.checkLabPointDistance(targetColorsLab[i])
                    if (dist < bestDist) { bestDist = dist; bestIdx = i }
                }
                // The chosen color from the palette
                val targetLab = targetColorsLab[bestIdx]
                // Save the index (0,1,2,...) to the result
                result[indexOfPixel] = targetIndices[bestIdx]

                // Calculate the error (difference between original and chosen color)
                val errorL = current.l - targetLab.l
                val errorA = current.a - targetLab.a
                val errorB = current.b - targetLab.b

                /**
                 * Function to spread error to a neighbor
                 * dx, dy = offset to neighbor (e.g., dx=1, dy=0 means right neighbor)
                 * */
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
                //Spread the error to four neighbors with Floyd-Steinberg weights
                distribute(1, 0, 7f / 16f)   // right neighbor (or left on odd rows)
                distribute(-1, 1, 3f / 16f)  // bottom-left neighbor
                distribute(0, 1, 5f / 16f)   // bottom neighbor
                distribute(1, 1, 1f / 16f)   // bottom-right neighbor
            }
        }
        return result
    }
}