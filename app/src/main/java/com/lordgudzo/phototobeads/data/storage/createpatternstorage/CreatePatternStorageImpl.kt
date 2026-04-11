package com.lordgudzo.phototobeads.data.storage.createpatternstorage

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.lordgudzo.phototobeads.data.storage.createpatternstorage.utils.ImageDecoder
import java.io.File

class CreatePatternStorageImpl(
    private val context: Context
) : CreatePatternStorageInterface  {

    private var cachedUri: Uri? = null

    /**
     * Take Uri content// -> save to cache as jpg --?? -> return Uri file//
     * Required for stable work with libraries that don't support content//
     * */
    override fun saveTempImage(uri: Uri): Uri? {
        try {
            //open access to file
            // val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            // Decode image from Uri !!!(weight RAM - ??)
            // val bitmap = BitmapFactory.decodeStream(inputStream)?: return null

            //Decode image from Uri with smaller size
            val bitmap = ImageDecoder().decodeSampledBitmap(uri, 1500, 1500, context) ?: return null

            //create empty file in cache
            val file = File(context.cacheDir, "temp_image.jpg")

            //Write bitmap to file
            file.outputStream().use { output ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)

                //guarantees writing of data from the buffer
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

    override fun getBitMapWithSize(gridSize: Int): Bitmap {
        return ImageDecoder().decodeSampledBitmap(cachedUri!!, gridSize, gridSize, context)!!
    }

    fun getPreviewImage(reqWidth: Int, reqHeight: Int) {

    }

    private fun clearCash() {
        cachedUri?.let {
            File(it.path ?: "").delete()
        }
        cachedUri = null
    }
}