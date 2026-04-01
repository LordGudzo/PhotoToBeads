package com.lordgudzo.phototobeads.domain.usecase

import android.net.Uri
import com.lordgudzo.phototobeads.domain.repository.CreatePatternRepository

/**
 * Take Uri content// -> save to cache -> return Uri file//
 * Required for stable work with libraries that don't support content//
 * */
class GetImageFromGalleryUseCase(private val createRepository: CreatePatternRepository) {
    fun execute(uri: Uri): Uri? {
        val result: Uri? = createRepository.saveImageToCash(uri)
        return result
    }

}