package com.lordgudzo.phototobeads.data

import android.net.Uri
import com.lordgudzo.phototobeads.data.storage.createpatternstorage.CreatePatternStorageInterface
import com.lordgudzo.phototobeads.domain.repository.CreatePatternRepository

class CreatePatternImpl(
    private val storage: CreatePatternStorageInterface
) : CreatePatternRepository {
    //Take Uri content// -> save to cache -> return Uri file//
    override fun saveImageToCash(uri: Uri): Uri? = storage.saveTempImage(uri)


    override fun getImage(): Uri? = storage.getTempImage()

    override fun createCroppedImageUri(): Uri {
        return storage.createTempCroppedImageUri()
    }

    override fun saveCropResult(resultUri: Uri): Uri {
       return storage.saveCropResult(resultUri)
    }
}