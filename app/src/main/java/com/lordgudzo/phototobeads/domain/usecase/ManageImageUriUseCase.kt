package com.lordgudzo.phototobeads.domain.usecase

import android.net.Uri
import com.lordgudzo.phototobeads.domain.repository.CreatePatternRepository

class ManageImageUriUseCase(private val createRepository: CreatePatternRepository) {

    fun saveImage(uri: Uri): Uri? {
        return createRepository.saveImage(uri)
    }

    fun getImage(): Uri? {
        return createRepository.getImage()
    }

}