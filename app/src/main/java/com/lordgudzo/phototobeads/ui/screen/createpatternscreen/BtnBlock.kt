package com.lordgudzo.phototobeads.ui.screen.createpatternscreen

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
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
fun BtnBlock(
    viewModel: CreatePatternViewModel,
    galleryLauncher: ManagedActivityResultLauncher<String, Uri?>
) {
    when (viewModel.patternState.value.step) {
        1 -> StepOneAddImage(viewModel, galleryLauncher )
        //    2 -> StepTwoCropImage(viewModel)

        else -> StepOneAddImage(viewModel, galleryLauncher )
    }
}

@Composable
private fun StepOneAddImage(
    viewModel: CreatePatternViewModel,
    galleryLauncher: ManagedActivityResultLauncher<String, Uri?>
) {
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


        if (viewModel.patternState.value.imageUri != null) {
            Spacer(modifier = Modifier.padding(8.dp))
            PrimaryActionButton(
                "Next: Crop Image",
                Icons.AutoMirrored.Filled.ArrowForward,
                color = MaterialTheme.colorScheme.secondary,
                {
                    viewModel.onStepChanged(2)
                })
        }
    }
}






