package com.lordgudzo.phototobeads.domain.imageprocessing

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.ColorUtils
import com.lordgudzo.phototobeads.domain.model.LabPoint

/**
 * This function converts a Bitmap image to LAB color points.
 *
 * Why we need this:
 * RGB color is good for screens, but not good for color comparison.
 * Two colors can look similar, but RGB values can be very different.
 *
 * LAB color is better for this:
 * Distance between colors in LAB ≈ how people see color difference.
 *
 * What this function does:
 * - Takes each pixel from the image
 * - Gets its RGB color (red, green, blue)
 * - Converts RGB → LAB (L, A, B values)
 * - Stores result as LabPoint object
 *
 * About LAB:
 * L = light (brightness)
 * A = green ↔ red
 * B = blue ↔ yellow
 *
 * Example:
 * Bitmap (pixels) → [color1, color2, color3]
 * ↓
 * LAB points → [(L1,A1,B1), (L2,A2,B2), (L3,A3,B3)]
 *
 * This data is used later for:
 * - k-means (find main colors)
 * - color matching
 * - dithering
 *
 *
 * @param bitmap - source image
 * @return Array of LabPoint (one LabPoint for each pixel)
 */
class BitMapToLabArray {
    fun apply(bitmap: Bitmap): Array<LabPoint> {
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
}