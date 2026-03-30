package com.lordgudzo.phototobeads.ui.screen.createpatternscreen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DescriptionBlock(viewModel: CreatePatternViewModel) {
    when(viewModel.patternState.value.step) {
        1 -> StepOneAddImage()
        2 -> StepTwoCropImage()

        else -> StepOneAddImage()
    }
}

@Composable
fun StepOneAddImage() {
    Spacer(modifier = Modifier.padding(15.dp))
    Text("Select Photo", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.padding(5.dp))
    Text("Choose an image to convert into a bead pattern", fontSize = 16.sp)
    Spacer(modifier = Modifier.padding(15.dp))
}

@Composable
fun StepTwoCropImage() {
    Spacer(modifier = Modifier.padding(15.dp))
    Text("Crop Image", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.padding(5.dp))
    Text("Adjust the crop area for your pattern", fontSize = 16.sp)
    Spacer(modifier = Modifier.padding(15.dp))
}
