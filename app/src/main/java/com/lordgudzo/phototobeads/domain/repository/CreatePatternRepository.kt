package com.lordgudzo.phototobeads.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import com.lordgudzo.phototobeads.domain.model.BeadColor

interface CreatePatternRepository {
    /**
     * Take Uri content// -> save to cache -> return Uri file//
     * Required for stable work with libraries that don't support content//
     * */
    fun saveImageToCash(uri: Uri): Uri?

    fun getImage(): Uri?

    fun createCroppedImageUri() : Uri
    fun saveCropResult(resultUri: Uri) : Uri

    fun getBitMapWithSize(gridSize: Int): Bitmap

    fun getPalette(palette: String): List<BeadColor>

}