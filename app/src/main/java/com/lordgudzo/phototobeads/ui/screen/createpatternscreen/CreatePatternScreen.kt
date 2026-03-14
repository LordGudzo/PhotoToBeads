package com.lordgudzo.phototobeads.ui.screen.createpatternscreen

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter


@Composable
fun CreatePatternScreen() {
    val viewModel: CreatePatternViewModel = viewModel()

    //<editor-fold desc="UI">
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Header(viewModel)

        //<editor-fold desc="Description block">
        Spacer(modifier = Modifier.padding(15.dp))
        Text("Select Photo", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.padding(5.dp))
        Text("Choose an image to convert into a bead pattern", fontSize = 16.sp)
        Spacer(modifier = Modifier.padding(15.dp))
        //</editor-fold>

        //<editor-fold desc="Image Field">
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            tonalElevation = 2.dp,
            shape = MaterialTheme.shapes.medium
        ) {
            Box(contentAlignment = Alignment.Center) {
                viewModel.selectedImageUri?.let { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize()
                    )
                } ?: Text("No image selected")
            }
        }
        Spacer(modifier = Modifier.padding(15.dp))
        //</editor-fold>

        BtnBlock(viewModel)
    }
    //</editor-fold>
}

@Preview(showBackground = true)
@Composable
fun CreatePatternPreview() {
    CreatePatternScreen()
}