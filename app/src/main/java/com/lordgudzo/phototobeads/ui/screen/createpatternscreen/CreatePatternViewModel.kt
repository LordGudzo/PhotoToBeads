package com.lordgudzo.phototobeads.ui.screen.createpatternscreen

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.yalantis.ucrop.UCrop
import java.io.File


class CreatePatternViewModel : ViewModel() {
    var activeStep by mutableIntStateOf(1)
        private set

    fun changeActiveStep(step: Int) {
        activeStep = step
    }


    var imageUri by mutableStateOf<Uri?>(null)
        private set

    fun updateImageUri(uri: Uri?) {
        imageUri = uri
    }

    private var _sourceTempFile by mutableStateOf<File?>(null)
    fun setSourceTempFile(file: File?) {
        _sourceTempFile = file
    }

    fun clearSourceTempFile() {
        _sourceTempFile?.delete()
        _sourceTempFile = null
    }


    fun handleCropResult(result: ActivityResult, context: Context) {
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                result.data?.let { intent ->
                    val uri = UCrop.getOutput(intent)
                    if (uri != null) updateImageUri(uri)
                }
                clearSourceTempFile()
            }

            UCrop.RESULT_ERROR -> {
                result.data?.let { intent ->
                    val error = UCrop.getError(intent)
                    Toast.makeText(context, "Crop error: ${error?.message}", Toast.LENGTH_LONG)
                        .show()
                }
                clearSourceTempFile()
            }

            else -> clearSourceTempFile()
        }
    }
}