package com.lordgudzo.phototobeads.data.storage.createpatternstorage

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import java.io.File

class CreatePatternStorageImpl(
    private val context: Context
) : CreatePatternStorageInterface  {

    private var cachedUri: Uri? = null

    override fun saveTempImage(uri: Uri): Uri? {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.cacheDir, "temp_image.jpg")

            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            cachedUri = file.toUri()
           return cachedUri
        } catch (e: Exception) {
            return null
        }
    }

    override fun getTempImage(): Uri? = cachedUri

    override fun clearCash() {
        cachedUri?.let {
            File(it.path ?: "").delete()
        }
        cachedUri = null
    }
}