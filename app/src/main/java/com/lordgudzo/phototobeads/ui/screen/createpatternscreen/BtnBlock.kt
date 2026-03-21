package com.lordgudzo.phototobeads.ui.screen.createpatternscreen

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
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
import com.lordgudzo.phototobeads.domain.usecase.ImageCropUseCase
import com.lordgudzo.phototobeads.ui.components.PrimaryActionButton
import java.io.File


@Composable
fun BtnBlock(
    viewModel: CreatePatternViewModel,
    cropLauncher: ActivityResultLauncher<Intent>,
    context: Context
) {
    when (viewModel.activeStep) {
        1 -> StepOneAddImage(viewModel)
        2 -> StepTwoCropImage(viewModel, cropLauncher, context)

        else -> StepOneAddImage(viewModel)
    }
}

@Composable
private fun StepOneAddImage(viewModel: CreatePatternViewModel) {
    // Launcher for selecting an image from the gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let { viewModel.updateImageUri(it) }
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


        if (viewModel.imageUri != null) {
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
private fun StepTwoCropImage(
    viewModel: CreatePatternViewModel,
    cropLauncher: ActivityResultLauncher<Intent>,
    context: Context
) {
    val sourceUri = viewModel.imageUri ?: return
    val imageCropUseCase = ImageCropUseCase()

    Column {
        Spacer(modifier = Modifier.padding(45.dp))
        PrimaryActionButton(
            "Crop Image",
            Icons.AutoMirrored.Filled.ArrowForward,
            color = MaterialTheme.colorScheme.secondary
        ) {
            try {
                // Create temp files in cacheDir (UI/ViewModel responsibility)
                val sourceFile = File(context.cacheDir, "source_${System.currentTimeMillis()}.jpg")
                val destFile = File(context.cacheDir, "cropped_${System.currentTimeMillis()}.jpg")

                // Open InputStream in UI, pass to UseCase
                context.contentResolver.openInputStream(sourceUri)?.use { inputStream ->
                    val success = imageCropUseCase.copyInputStreamToFile(inputStream, sourceFile)
                    if (!success) {
                        Toast.makeText(context, "Cannot load image", Toast.LENGTH_SHORT).show()
                        return@PrimaryActionButton
                    }
                } ?: run {
                    Toast.makeText(context, "Cannot open image", Toast.LENGTH_SHORT).show()
                    return@PrimaryActionButton
                }

                // Save sourceFile to ViewModel for later cleanup
                viewModel.setSourceTempFile(sourceFile)

                // Create UCrop object (UseCase is pure)
                val uCrop = imageCropUseCase.createCropIntent(sourceFile, destFile)
                cropLauncher.launch(uCrop.getIntent(context))
            } catch (e: Exception) {
                Toast.makeText(context, "Error preparing crop", Toast.LENGTH_SHORT).show()
            }
        }
    }
}





