package com.lordgudzo.phototobeads.ui.screen.createpatternscreen

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.lordgudzo.phototobeads.domain.model.CropRequest
import com.lordgudzo.phototobeads.domain.usecase.CropImageUseCase
import com.lordgudzo.phototobeads.domain.usecase.GeneratingPatternForBeadsUseCase
import com.lordgudzo.phototobeads.domain.usecase.GetImageFromGalleryUseCase


class CreatePatternViewModel(
    private val getImageFromGalleryUseCase: GetImageFromGalleryUseCase,
    private val cropImageUseCase: CropImageUseCase,
    private val generatingPattern: GeneratingPatternForBeadsUseCase
) : ViewModel() {
    //<editor-fold desc="CreatePatternState">
    // ---Image state---
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
    //</editor-fold>

    //<editor-fold desc="Ucrop">
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
    //</editor-fold>

    //<editor-fold desc="SettingsState">
    private val _settingsState = mutableStateOf(SettingsState())
    val settingsState: State<SettingsState> = _settingsState


    fun onGridSizeChanged(newGirdSize: Int) {
        _settingsState.value = _settingsState.value.copy(
            gridSize = newGirdSize
        )
    }
    fun onColorCountChanged(newColorCount: Int) {
        _settingsState.value = _settingsState.value.copy(
            colorCount = newColorCount
        )
    }
    fun onDitherStrengthChanged(newDitherStrength: Float) {
        _settingsState.value = _settingsState.value.copy(
            ditherStrength = newDitherStrength
        )
    }
    fun onMenuExpandedChanged(newValue: Boolean) {
        _settingsState.value = _settingsState.value.copy(
            menuExpanded = newValue
        )
    }
    fun onSelectedPaletteChanged(newPalette: String) {
        _settingsState.value = _settingsState.value.copy(
            selectedPalette = newPalette
        )
    }
    //</editor-fold>

    var resultBitmap by mutableStateOf<Bitmap?>(null)
        private set

    fun setBitMap() {
        resultBitmap = generatingPattern.execute(
            gridSize = _settingsState.value.gridSize,
            colorCount = _settingsState.value.colorCount,
            ditherStrength = _settingsState.value.ditherStrength,
            palette = _settingsState.value.selectedPalette

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

data class SettingsState(
    val gridSize: Int = 200,
    val colorCount: Int = 20,
    val ditherStrength: Float = 0.5f,
    val paletteOptions: List<String> = listOf<String>("Preciosa", "default"),
    val selectedPalette: String = "Preciosa",
    val menuExpanded: Boolean = false
)