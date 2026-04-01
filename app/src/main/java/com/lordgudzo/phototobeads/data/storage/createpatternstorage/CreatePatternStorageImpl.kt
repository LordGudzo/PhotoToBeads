package com.lordgudzo.phototobeads.data.storage.createpatternstorage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import java.io.File

class CreatePatternStorageImpl(
    private val context: Context
) : CreatePatternStorageInterface  {

    private var cachedUri: Uri? = null

    /**
     * Take Uri content// -> save to cache -> return Uri file//
     * Required for stable work with libraries that don't support content//
     * */
    override fun saveTempImage(uri: Uri): Uri? {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return null

            val bitmap = BitmapFactory.decodeStream(inputStream)
                ?: return null

            val file = File(context.cacheDir, "temp_image.jpg")

            file.outputStream().use { output ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)
                output.flush()
            }

            if (file.length() == 0L) return null

            cachedUri = file.toUri()
            return  cachedUri

        } catch (e: Exception) {
            Log.e("Error", "saveTempImage failed", e)
            return null
        }
    }



    override fun getTempImage(): Uri? = cachedUri

    override fun createTempCroppedImageUri(): Uri {
        val file = File(context.cacheDir, "temp_crop_${System.currentTimeMillis()}.jpg")
        return file.toUri()
    }

    override fun saveCropResult(resultUri: Uri): Uri  {
        clearCash()
        cachedUri = resultUri
        return cachedUri!!
    }

    private fun clearCash() {
        cachedUri?.let {
            File(it.path ?: "").delete()
        }
        cachedUri = null
    }
}