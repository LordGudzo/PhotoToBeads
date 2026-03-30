package com.lordgudzo.phototobeads.data

import android.net.Uri
import com.lordgudzo.phototobeads.data.storage.createpatternstorage.CreatePatternStorageInterface
import com.lordgudzo.phototobeads.domain.repository.CreatePatternRepository

class CreatePatternImpl(
    private val storage: CreatePatternStorageInterface   // ← правильная зависимость
) : CreatePatternRepository {
    override fun saveImage(uri: Uri): Uri? = storage.saveTempImage(uri)
    override fun getImage(): Uri? = storage.getTempImage()
    override fun clearCash() = storage.clearCash()
}