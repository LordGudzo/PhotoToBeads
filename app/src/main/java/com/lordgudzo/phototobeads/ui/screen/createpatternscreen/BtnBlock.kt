package com.lordgudzo.phototobeads.ui.screen.createpatternscreen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lordgudzo.phototobeads.ui.components.PrimaryActionButton

@Composable
fun BtnBlock(viewModel: CreatePatternViewModel) {
    val step = viewModel.activeStep

    when(step) {
        1 -> StepOneAddImage(viewModel)
        2 -> StepTwoCropImage(viewModel)

        else -> StepOneAddImage(viewModel)
    }





}

@Composable
private fun StepOneAddImage(viewModel: CreatePatternViewModel) {
    // Launcher for selecting an image from the gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let { viewModel.updateSelectedImageUri(it) }
        }
    )
    //<editor-fold desc="BTN-field">
    Column {
        PrimaryActionButton(
            "Choose from Gallery",
            Icons.Default.PhotoLibrary,
            color = MaterialTheme.colorScheme.primary,
            { galleryLauncher.launch("image/*") })

        PrimaryActionButton(
            "Take a photo",
            Icons.Default.CameraAlt,
            color = Color.Red,
            {})


        if (viewModel.selectedImageUri != null) {
            Spacer(modifier = Modifier.padding(8.dp))
            PrimaryActionButton(
                "Next: Crop Image",
                Icons.AutoMirrored.Filled.ArrowForward,
                color = MaterialTheme.colorScheme.secondary,
                {
                    viewModel.changeActiveStep(2)
                })
        }
    }
}
@Composable
private fun StepTwoCropImage(viewModel: CreatePatternViewModel) {
    Column {
        PrimaryActionButton(
            "Next: Setting",
            Icons.AutoMirrored.Filled.ArrowForward,
            color = MaterialTheme.colorScheme.secondary,
            {viewModel.changeActiveStep(3)})
    }
}