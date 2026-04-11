package com.lordgudzo.phototobeads.data.storage.createpatternstorage.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri

/**
 * Decode image from Uri with smaller size.
 * This helps to save memory.
 * How it works:
 * 1. Gets size of image without load bitmap.
 * 2. Calculate scale factor.
 * 3. Upload a reduced size image as bitmap
 *
 * @param uri - image source
 * @param reqWidth / reqHeight - needed size
 * @return Bitmap scaled close to requested size (not exact)
 */
class ImageDecoder {

    fun decodeSampledBitmap(uri: Uri, reqWidth: Int, reqHeight: Int, context: Context): Bitmap? {
        // Used to open image data by Uri (from gallery or other apps)
        val resolver = context.contentResolver

        // Step 1: get image size
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true  // Tell system: don't load bitmap, only read image size (width and height)
        }
        resolver.openInputStream(uri)?.use {
            //Decode image but don't create bitmap, only fill options with size
            BitmapFactory.decodeStream(it, null, options)
        }

        // Step 2: calculate scale factor
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        // Step 3: decode real bitmap with scale
        options.inJustDecodeBounds = false //close command to option only read width and height

        //Now decode real bitmap using calculated scale (inSampleSize)
        return resolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        }
    }

    /**
     * Calculate how many times the image needs to be reduced that it reaches the desired size.
     * For example: calculateInSampleSize = 2 → the image will be 2 times smaller.
     *
     * @param options  options.outHeight and options.outWidth (height and width original image)
     * */
    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val (height, width) = options.outHeight to options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while (halfHeight / inSampleSize >= reqHeight &&
                halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
}