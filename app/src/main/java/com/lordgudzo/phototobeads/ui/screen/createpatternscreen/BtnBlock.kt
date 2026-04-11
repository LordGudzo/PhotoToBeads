package com.lordgudzo.phototobeads.ui.screen.createpatternscreen

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lordgudzo.phototobeads.domain.model.CropRequest
import com.lordgudzo.phototobeads.ui.components.PrimaryActionButton
import com.yalantis.ucrop.UCrop
import kotlin.math.roundToInt


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
        3 -> StepThreeSettings(viewModel)
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
        Spacer(modifier = Modifier.padding(15.dp))

        PrimaryActionButton(
            "Crop Image",
            Icons.AutoMirrored.Filled.ArrowForward,
            color = Color.Red,
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
        Spacer(modifier = Modifier.padding(30.dp))
        PrimaryActionButton(
            "Next: Settings/preview",
            Icons.AutoMirrored.Filled.ArrowForward,
            color = MaterialTheme.colorScheme.secondary,
            {
                viewModel.onStepChanged(3)
            })
    }
}

@Composable
fun StepThreeSettings(
    viewModel: CreatePatternViewModel
) {
    val paletteOptions = viewModel.settingsState.value.paletteOptions
    val selectedPalette = viewModel.settingsState.value.selectedPalette
    val menuExpanded = viewModel.settingsState.value.menuExpanded

    Column {
        Spacer(modifier = Modifier.padding(15.dp))
        //<editor-fold desc="Size Setting">
        Text(
            text = "Grid size: ${viewModel.settingsState.value.gridSize} (50–500)",
            style = MaterialTheme.typography.bodyLarge
        )
        Slider(
            value = viewModel.settingsState.value.gridSize.toFloat(),
            onValueChange = { viewModel.onGridSizeChanged(it.roundToInt()) },
            valueRange = 50f..500f,
            steps = 449
        )
        //</editor-fold>

        //<editor-fold desc="Colors Count Setting">
        Column {
            Text(
                text = "Color count: ${viewModel.settingsState.value.colorCount} (2–100)",
                style = MaterialTheme.typography.bodyLarge
            )
            Slider(
                value = viewModel.settingsState.value.colorCount.toFloat(),
                onValueChange = { viewModel.onColorCountChanged(it.roundToInt()) },
                valueRange = 2f..100f,
                steps = 97   // 100-2 = 98, минус 1 = 97 шагов
            )
        }
        //</editor-fold>

        //<editor-fold desc="Dither strength">
        Column {
            Text(
                text = "Dither strength: %.2f".format(viewModel.settingsState.value.ditherStrength),
                style = MaterialTheme.typography.bodyLarge
            )
            Slider(
                value = viewModel.settingsState.value.ditherStrength,
                onValueChange = { viewModel.onDitherStrengthChanged(it) },
                valueRange = 0f..1f
            )
        }
        //</editor-fold>

        //<editor-fold desc="Thread palette">
        Column {
            Text(
                text = "Thread palette",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Button(
                onClick = { viewModel.onMenuExpandedChanged(true) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(selectedPalette)
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }


            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { viewModel.onMenuExpandedChanged(false) }
            ) {
                paletteOptions.forEach { palette ->
                    DropdownMenuItem(
                        text = { Text(palette) },
                        onClick = {
                            viewModel.onSelectedPaletteChanged(palette)
                            viewModel.onMenuExpandedChanged(false)
                        }
                    )
                }
            }
        }
        //</editor-fold>

        Spacer(modifier = Modifier.padding(10.dp))


        PrimaryActionButton(
            "Next: Get Result",
            Icons.AutoMirrored.Filled.ArrowForward,
            color = MaterialTheme.colorScheme.secondary,
            {
                viewModel.setBitMap()
                viewModel.onStepChanged(4)
            })
    }
}




