package com.lordgudzo.phototobeads.domain.usecase

import android.net.Uri
import com.lordgudzo.phototobeads.domain.model.CropRequest
import com.lordgudzo.phototobeads.domain.repository.CreatePatternRepository

class CropImageUseCase(private val createRepository: CreatePatternRepository) {

    fun getCropRequest(): CropRequest? {
        val inputUri = createRepository.getImage() ?: return null

        val outputFile = createRepository.createCroppedImageUri()


        return CropRequest(
            inputUri = inputUri,
            outputUri = outputFile,
            aspectX = 1f,
            aspectY = 1f,
            maxWidth = 1500,
            maxHeight = 1500,
            quality = 90
        )
    }

    fun saveCropResult(resultUri: Uri): Uri {
        return createRepository.saveCropResult(resultUri)
    }
}