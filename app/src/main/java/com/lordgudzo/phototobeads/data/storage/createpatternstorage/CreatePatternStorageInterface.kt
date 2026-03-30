package com.lordgudzo.phototobeads.data.storage.createpatternstorage

import android.net.Uri

interface CreatePatternStorageInterface {
    fun saveTempImage(uri: Uri): Uri?
    fun getTempImage(): Uri?
    fun clearCash()
}