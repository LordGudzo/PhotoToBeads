package com.lordgudzo.phototobeads.ui.screen.createpatternscreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lordgudzo.phototobeads.data.CreatePatternImpl
import com.lordgudzo.phototobeads.data.storage.createpatternstorage.CreatePatternStorageImpl
import com.lordgudzo.phototobeads.data.storage.createpatternstorage.CreatePatternStorageInterface
import com.lordgudzo.phototobeads.domain.repository.CreatePatternRepository
import com.lordgudzo.phototobeads.domain.usecase.CropImageUseCase
import com.lordgudzo.phototobeads.domain.usecase.GetImageFromGalleryUseCase

class CreatePatternViewModelFactory (context: Context) : ViewModelProvider.Factory {
    val storage: CreatePatternStorageInterface = CreatePatternStorageImpl(context)
    val repository: CreatePatternRepository = CreatePatternImpl(storage)
    val getImageFromGalleryUseCase = GetImageFromGalleryUseCase(repository)
    val cropImageUseCase = CropImageUseCase(repository)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CreatePatternViewModel(
            getImageFromGalleryUseCase = getImageFromGalleryUseCase,
            cropImageUseCase = cropImageUseCase
        ) as T
    }
}