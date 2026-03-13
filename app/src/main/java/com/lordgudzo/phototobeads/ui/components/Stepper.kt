package com.lordgudzo.phototobeads.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun Stepper(
    currentStep: Int,
    totalSteps: Int = 4,
    onStepClick: (Int) -> Unit = {}
) {
    Log.d("TEST", "Stepper: $currentStep")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        for (step in 1..totalSteps) {
            StepCircle(
                step = step,
                isActive = step == currentStep,
                onClick = { onStepClick(step) }
            )

            //<editor-fold desc="lines between circles">
            if (step < totalSteps) {
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .padding(horizontal = 8.dp)
                        .background(Color.LightGray)
                )
            }
            //</editor-fold>
        }
    }
}

@Composable
fun StepCircle(
    step: Int,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isActive) Color.Red else MaterialTheme.colorScheme.primary
    Button(
        onClick = { onClick() },
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = step.toString())
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StepperPreview() {
    Stepper(1)
}