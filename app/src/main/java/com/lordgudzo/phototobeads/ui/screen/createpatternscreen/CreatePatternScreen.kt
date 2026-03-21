package com.lordgudzo.phototobeads.ui.screen.createpatternscreen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter


@Composable
fun CreatePatternScreen() {
    val viewModel: CreatePatternViewModel = viewModel()
    val context = LocalContext.current

    //<editor-fold desc="Launcher for get results from uCrop">
    val cropLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result -> viewModel.handleCropResult(result, context) }
    //</editor-fold>


    //<editor-fold desc="UI">
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Header(viewModel)
        DescriptionBlock(viewModel)

        //<editor-fold desc="ImageBlock">
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            tonalElevation = 2.dp,
            shape = MaterialTheme.shapes.medium
        ) {
            Box(contentAlignment = Alignment.Center) {
                viewModel.imageUri?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } ?: Text("No image selected")
            }
        }
        //</editor-fold>

        Spacer(modifier = Modifier.padding(15.dp))
        BtnBlock(viewModel, cropLauncher, context)
    }
    //</editor-fold>
}

@Preview(showBackground = true)
@Composable
fun CreatePatternPreview() {
    CreatePatternScreen()
}