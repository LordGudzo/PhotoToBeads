package com.lordgudzo.phototobeads.ui.screen.createpatternscreen.blocks

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lordgudzo.phototobeads.ui.components.Stepper
import com.lordgudzo.phototobeads.ui.screen.createpatternscreen.CreatePatternViewModel


@Composable
fun HeaderBlock(viewModel: CreatePatternViewModel) {
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

    HorizontalDivider(
        color = Color.Gray,
        thickness = 1.dp,
        modifier = Modifier.padding(horizontal = 12.dp)
    )

    Stepper(
        currentStep = viewModel.patternState.value.step,
        onStepClick = { viewModel.onStepChanged(it) }
    )
}




