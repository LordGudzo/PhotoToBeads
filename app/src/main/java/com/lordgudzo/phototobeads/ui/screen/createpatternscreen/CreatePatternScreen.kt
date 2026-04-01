package com.lordgudzo.phototobeads.ui.screen.createpatternscreen

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yalantis.ucrop.UCrop


@Composable
fun CreatePatternScreen() {

    //<editor-fold desc="Initialization">
    val context = LocalContext.current

    val viewModel: CreatePatternViewModel = viewModel(
        factory = CreatePatternViewModelFactory(context = context)
    )

    //<editor-fold desc="Launcher for selecting an image from the gallery">
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                viewModel.onImageSelected(it)
            } ?: Toast.makeText(context, "Failed to load image", Toast.LENGTH_LONG).show()
        }
    )
    //</editor-fold>

    //<editor-fold desc="Launcher for get results from uCrop">
    val cropLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            result.data?.let { data ->
                val resultUri = UCrop.getOutput(data)
                viewModel.saveCropResult(resultUri!!)
            }

        } else {
            Log.e("Error", "CreatePatternScreen: $result", )
            Toast.makeText(context, "Failed to crop image", Toast.LENGTH_LONG).show()
        }

    }
    //</editor-fold>
    //</editor-fold>


    //<editor-fold desc="UI">
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        HeaderBlock(viewModel)

        DescriptionBlock(viewModel)

        ImageBlock(
            viewModel.patternState.value.imageUri,
            modifier = Modifier.weight(1f)
        )

        BtnBlock(viewModel, galleryLauncher, cropLauncher, context)
    }
    //</editor-fold>

}

@Preview(showBackground = true)
@Composable
fun CreatePatternPreview() {
    CreatePatternScreen()
}