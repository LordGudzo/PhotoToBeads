package com.lordgudzo.phototobeads.domain.imageprocessing

import android.graphics.Bitmap
import android.graphics.Color

/**
 * This function smooths an image.
 * It removes small noise (bad pixels).
 * It uses a median filter (3x3 area).
 * Red Red Red            Red Red Red
 * Red BLUE! Red  after   Red Red Red
 * Red Red Red            Red Red Red
 *
 * @param bitmap - input image
 * @param level - how many times we apply the filter
 *
 * @return new smoothed bitmap without (bad pixels)
 */
class MedianFilter {
    fun apply(bitmap: Bitmap, level: Int): Bitmap {
        /**
         *  Create a copy of the original bitmap
         *
         *  bitmap.copy(config, isMutable)
         *
         *  Config.ARGB_8888:
         *  A = alpha (transparency)
         *  R = red, G = green, B = blue
         *  8 bits per channel → good quality colors
         *
         *  true: means the bitmap is mutable (we CAN change pixels)
         *  If false → bitmap is read-only (we cannot edit it)   */
        val result = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        // Get image size
        val width = bitmap.width
        val height = bitmap.height

        // Create array to store all pixels. Each Int = one pixel color (ARGB packed into one number)
        val pixels = IntArray(width * height)

        /**
         *  Copy pixels from bitmap into array
         *
         *  getPixels(dst, offset, stride, x, y, width, height)
         *  dst = pixels array (where to write)
         *  offset = 0 → start from beginning of array
         *  stride = width → how many pixels in one row
         *  x, y = 0,0 → start from top-left corner
         *  width, height → take full image  */
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        // Repeat filter "level" times. More level → stronger smoothing.
        repeat(level) {
            // Make a copy of pixels
            // IMPORTANT:
            // temp = source (read from here)
            // pixels = result (write here)
            // If we do not copy:
            // filter will use already changed pixels → wrong result
            val temp = pixels.clone()

            // Loop over image (skip borders) because edges do not have full 3x3 neighbors
            for (row in 1 until height - 1) {
                for (col in 1 until width - 1) {
                    // Lists to store color values from neighbors
                    val reds = mutableListOf<Int>()
                    val greens = mutableListOf<Int>()
                    val blues = mutableListOf<Int>()

                    // Loop over 3x3 area (neighbors)
                    //
                    // rowOffset = change in Y (-1, 0, 1)
                    // colOffset = change in X (-1, 0, 1)
                    //
                    // This covers:
                    // top-left, top, top-right
                    // left, center, right
                    // bottom-left, bottom, bottom-right
                    for (rowOffset in -1..1) {
                        for (colOffset in -1..1) {
                            // Calculate neighbor position
                            val neighborRow = row + rowOffset
                            val neighborCol = col + colOffset

                            // Convert 2D position → 1D index
                            val neighborIndex = neighborRow * width + neighborCol

                            // Get neighbor pixel color
                            val neighborColor = temp[neighborIndex]


                            // Take color channels
                            reds.add(Color.red(neighborColor))
                            greens.add(Color.green(neighborColor))
                            blues.add(Color.blue(neighborColor))
                        }
                    }
                    // Sort values (from small to big)
                    reds.sort()
                    greens.sort()
                    blues.sort()

                    // Take median (middle value)
                    val medianRed = reds[4]
                    val medianGreen = greens[4]
                    val medianBlue = blues[4]

                    // Write new pixel
                    val currentIndex = row * width + col
                    pixels[currentIndex] = Color.rgb(medianRed, medianGreen, medianBlue)
                }
            }
        }

        // Write pixels back to bitmap
        result.setPixels(pixels, 0, width, 0, 0, width, height)
        return result
    }
}
