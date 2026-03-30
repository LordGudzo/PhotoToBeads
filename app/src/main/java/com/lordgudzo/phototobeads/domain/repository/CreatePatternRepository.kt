package com.lordgudzo.phototobeads.domain.repository

import android.net.Uri

interface CreatePatternRepository {
    fun saveImage(uri: Uri): Uri?
    fun getImage(): Uri?
    fun clearCash()
}