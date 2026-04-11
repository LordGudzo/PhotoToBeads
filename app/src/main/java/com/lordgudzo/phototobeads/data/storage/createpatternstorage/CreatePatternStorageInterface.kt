package com.lordgudzo.phototobeads.data.storage.createpatternstorage

import android.graphics.Bitmap
import android.net.Uri

interface CreatePatternStorageInterface {
    // Take Uri content// -> save to cache -> return Uri file//
    fun saveTempImage(uri: Uri): Uri?


    fun getTempImage(): Uri?

    fun createTempCroppedImageUri() : Uri
    fun saveCropResult(resultUri: Uri): Uri

    fun getBitMapWithSize(gridSize: Int): Bitmap
}