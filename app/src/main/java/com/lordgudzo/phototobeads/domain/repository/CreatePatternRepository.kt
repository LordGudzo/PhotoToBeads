package com.lordgudzo.phototobeads.domain.repository

import android.net.Uri

interface CreatePatternRepository {
    /**
     * Take Uri content// -> save to cache -> return Uri file//
     * Required for stable work with libraries that don't support content//
     * */
    fun saveImageToCash(uri: Uri): Uri?

    fun getImage(): Uri?

    fun createCroppedImageUri() : Uri
    fun saveCropResult(resultUri: Uri) : Uri

}