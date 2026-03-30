package com.lordgudzo.phototobeads.ui.screen.createpatternscreen

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.lordgudzo.phototobeads.domain.usecase.ManageImageUriUseCase


class CreatePatternViewModel(
    private val imageManagerUseCase: ManageImageUriUseCase
) : ViewModel() {
    // --- STATE ---
    private val _patternState = mutableStateOf(CreatePatternState())
    val patternState: State<CreatePatternState> = _patternState

    // --- UI EVENTS ---

    fun onStepChanged(step: Int) {
        _patternState.value = _patternState.value.copy(step = step)
    }

    fun onImageSelected(uri: Uri) {
        val result = imageManagerUseCase.saveImage(uri)
        result?.let {
            _patternState.value = _patternState.value.copy(
                imageUri = result
            )
        }
    }
}

/**
 * @param step - responsible for rendering the screen depending on the step
 * */
data class CreatePatternState(
    val step: Int = 1,
    val imageUri: Uri? = null
)