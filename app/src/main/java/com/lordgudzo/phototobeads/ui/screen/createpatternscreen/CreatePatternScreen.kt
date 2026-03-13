package com.lordgudzo.phototobeads.ui.screen.createpatternscreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lordgudzo.phototobeads.ui.components.PrimaryActionButton
import com.lordgudzo.phototobeads.ui.components.Stepper


@Composable
fun CreatePatternScreen() {
    val createScreenViewModel: CreatePatternViewModel = viewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        //<editor-fold desc="BackBtn">
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go Back"
                )
            }
            Text("Create Pattern", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        //</editor-fold>
        HorizontalDivider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        StepsOfCreate(createScreenViewModel)
        Spacer(modifier = Modifier.padding(15.dp))

        Text("Select Photo", fontSize = 18.sp, fontWeight = FontWeight.Bold )
        Spacer(modifier = Modifier.padding(5.dp))
        Text("Choose an image to convert into a bead pattern", fontSize = 16.sp)

        Spacer(modifier = Modifier.padding(15.dp))
        //<editor-fold desc="Image Field">
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            tonalElevation = 2.dp,
            shape = MaterialTheme.shapes.medium
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("No image selected")
            }
        }
        //</editor-fold>
        Spacer(modifier = Modifier.padding(15.dp))
        //<editor-fold desc="BTN-field">
        Column{
            PrimaryActionButton(
                "Choose from Gallery",
                Icons.Default.PhotoLibrary,
                color = MaterialTheme.colorScheme.primary,
                {})

            PrimaryActionButton(
                "Take a photo",
                Icons.Default.CameraAlt,
                color = Color.Red,
                {})
        }
        //</editor-fold>
        Spacer(modifier = Modifier.padding(15.dp))
    }
}

@Composable
fun StepsOfCreate(viewModel: CreatePatternViewModel) {
    Stepper(
        currentStep = viewModel.activeStep,
        onStepClick = { viewModel.changeActiveStep(it) }
    )
}




@Preview(showBackground = true)
@Composable
fun CreatePatternPreview() {
    CreatePatternScreen()
}