package com.lordgudzo.phototobeads.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.lordgudzo.phototobeads.ui.screen.createpatternscreen.CreatePatternScreen
import com.lordgudzo.phototobeads.ui.theme.PhotoToBeadsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhotoToBeadsTheme {
                CreatePatternScreen()
            }
        }
    }
}