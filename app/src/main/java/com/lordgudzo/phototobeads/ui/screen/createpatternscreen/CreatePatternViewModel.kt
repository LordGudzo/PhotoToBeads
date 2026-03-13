package com.lordgudzo.phototobeads.ui.screen.createpatternscreen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CreatePatternViewModel: ViewModel() {
    var activeStep by mutableIntStateOf(1)
        private set

    fun changeActiveStep(step: Int) {
        activeStep = step
        Log.d("TEST", "setActiveStep: $step  $activeStep")
    }
}