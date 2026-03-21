package com.lordgudzo.phototobeads.domain.usecase

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.yalantis.ucrop.UCrop
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ImageCropUseCase {

    /**
     * Copy image from input stream to destination file
     * inputStream must be opened in UI/ViewModel
     */
    fun copyInputStreamToFile(inputStream: InputStream, destFile: File): Boolean {
        return try {
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            if (bitmap == null) {
                Log.e("ImageCropUseCase", "BitmapFactory.decodeStream returned null")
                return false
            }
            FileOutputStream(destFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            bitmap.recycle()
            Log.d("ImageCropUseCase", "Bitmap saved to ${destFile.absolutePath}")
            true
        } catch (e: Exception) {
            Log.e("ImageCropUseCase", "Error copying bitmap to file", e)
            false
        }
    }

    /**
     * Create UCrop object from source and destination files
     * No Android Context needed here
     */
    fun createCropIntent(sourceFile: File, destFile: File): UCrop {
        val options = UCrop.Options().apply {
            setCompressionFormat(Bitmap.CompressFormat.JPEG)
            setCompressionQuality(90)
        }
        return UCrop.of(Uri.fromFile(sourceFile), Uri.fromFile(destFile))
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1500, 1500)
            .withOptions(options)
    }
}