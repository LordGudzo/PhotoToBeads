package com.lordgudzo.phototobeads.ui.screen.createpatternscreen

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.lordgudzo.phototobeads.domain.model.CropRequest
import com.lordgudzo.phototobeads.ui.components.PrimaryActionButton
import com.yalantis.ucrop.UCrop


@Composable
fun BtnBlock(
    viewModel: CreatePatternViewModel,
    galleryLauncher: ManagedActivityResultLauncher<String, Uri?>,
    cropLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    context: Context
) {
    when (viewModel.patternState.value.step) {
        1 -> StepOneAddImage(viewModel, galleryLauncher)
        2 -> StepTwoCropImage(viewModel, cropLauncher, context)

        else -> StepOneAddImage(viewModel, galleryLauncher)
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

@Composable
private fun StepTwoCropImage(
    viewModel: CreatePatternViewModel,
    cropLauncher: ActivityResultLauncher<Intent>,
    context: Context
) {
    Column {
        Spacer(modifier = Modifier.padding(45.dp))

        PrimaryActionButton(
            "Crop Image",
            Icons.AutoMirrored.Filled.ArrowForward,
            color = MaterialTheme.colorScheme.secondary,
            onClick = {
                val cropRequest: CropRequest? = viewModel.onCropButtonClicked()

                if (cropRequest != null) {
                    val options = UCrop.Options().apply {
                        setCompressionFormat(Bitmap.CompressFormat.JPEG)
                        setCompressionQuality(cropRequest.quality)
                    }
                    val intent = UCrop.of(cropRequest.inputUri, cropRequest.outputUri)
                        .withAspectRatio(cropRequest.aspectX, cropRequest.aspectY)
                        .withMaxResultSize(cropRequest.maxWidth, cropRequest.maxHeight)
                        .withOptions(options)
                        .getIntent(context)


                    cropLauncher.launch(intent)

                } else Toast.makeText(context, "Failed to crop image", Toast.LENGTH_LONG).show()
            }
        )
    }
}




