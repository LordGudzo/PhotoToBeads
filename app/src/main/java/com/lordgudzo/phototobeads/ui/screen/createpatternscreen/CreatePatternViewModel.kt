package com.lordgudzo.phototobeads.ui.screen.createpatternscreen

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.lordgudzo.phototobeads.domain.model.CropRequest
import com.lordgudzo.phototobeads.domain.usecase.CropImageUseCase
import com.lordgudzo.phototobeads.domain.usecase.GetImageFromGalleryUseCase


class CreatePatternViewModel(
    private val getImageFromGalleryUseCase: GetImageFromGalleryUseCase,
    private val cropImageUseCase: CropImageUseCase
) : ViewModel() {


    // --- STATE ---
    private val _patternState = mutableStateOf(CreatePatternState())
    val patternState: State<CreatePatternState> = _patternState


    // Steps for created new scheme
    fun onStepChanged(step: Int) {
        _patternState.value = _patternState.value.copy(step = step)
    }
    //adds new image from Gallery
    fun onImageSelected(uri: Uri) {
        val result = getImageFromGalleryUseCase.execute(uri)
        result?.let {
            _patternState.value = _patternState.value.copy(
                imageUri = result
            )
        }
    }

    fun onCropButtonClicked(): CropRequest? {
        _patternState.value.imageUri?.let {
            return cropImageUseCase.getCropRequest()
        }
        return null
    }

    fun saveCropResult(resultUri: Uri) {
        val cashUri = cropImageUseCase.saveCropResult(resultUri)
        _patternState.value = _patternState.value.copy(
            imageUri = cashUri
        )
    }


}

/**
 * @param step - responsible for rendering the screen depending on the step
 * */
data class CreatePatternState(
    val step: Int = 1,
    val imageUri: Uri? = null
)