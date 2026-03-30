package com.lordgudzo.phototobeads.ui.screen.createpatternscreen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lordgudzo.phototobeads.data.CreatePatternImpl
import com.lordgudzo.phototobeads.data.storage.createpatternstorage.CreatePatternStorageImpl
import com.lordgudzo.phototobeads.data.storage.createpatternstorage.CreatePatternStorageInterface
import com.lordgudzo.phototobeads.domain.repository.CreatePatternRepository
import com.lordgudzo.phototobeads.domain.usecase.ManageImageUriUseCase


@Composable
fun CreatePatternScreen() {
    //<editor-fold desc="Initialization">
    val context = LocalContext.current

    val storage: CreatePatternStorageInterface = CreatePatternStorageImpl(context)
    val repository: CreatePatternRepository = CreatePatternImpl(storage)
    val useCase = ManageImageUriUseCase(repository)

    val factory = remember { object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return CreatePatternViewModel(useCase) as T
        }
    } }

    val viewModel: CreatePatternViewModel = viewModel(factory = factory)

    // Launcher for selecting an image from the gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let { viewModel.onImageSelected(it) }
        }
    )
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

        BtnBlock(viewModel, galleryLauncher)
    }
    //</editor-fold>

}

@Preview(showBackground = true)
@Composable
fun CreatePatternPreview() {
    CreatePatternScreen()
}