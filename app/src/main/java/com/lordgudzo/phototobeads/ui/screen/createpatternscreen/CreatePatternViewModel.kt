package com.lordgudzo.phototobeads.ui.screen.createpatternscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import android.net.Uri
import androidx.compose.runtime.mutableStateOf

class CreatePatternViewModel: ViewModel() {
    var activeStep by mutableIntStateOf(1)
        private set

    var selectedImageUri by mutableStateOf<Uri?>(null)
        private set

    fun changeActiveStep(step: Int) {
        activeStep = step
    }

    fun updateSelectedImageUri(uri: Uri?) {
        selectedImageUri = uri
    }
}