package com.lordgudzo.phototobeads.data.storage.createpatternstorage.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.graphics.scale

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
        val resolver = context.contentResolver

        // Step 1: read size only
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        resolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        }

        // Step 2: calculate rough scale (powers of 2)
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        options.inJustDecodeBounds = false

        // Step 3: decode rough bitmap
        val roughBitmap = resolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        } ?: return null

        // Step 4: precise scale to exact reqWidth x reqHeight
        // filter=true = bilinear interpolation, much better than nearest neighbor
        return if (roughBitmap.width == reqWidth && roughBitmap.height == reqHeight) {
            roughBitmap // already exact, skip
        } else {
            val precise = roughBitmap.scale(reqWidth, reqHeight)
            roughBitmap.recycle() // free memory from rough bitmap
            precise
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