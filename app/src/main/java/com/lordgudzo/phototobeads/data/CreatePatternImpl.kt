package com.lordgudzo.phototobeads.data

import android.graphics.Bitmap
import android.net.Uri
import com.lordgudzo.phototobeads.data.mapper.toDomain
import com.lordgudzo.phototobeads.data.storage.createpatternstorage.CreatePatternStorageInterface
import com.lordgudzo.phototobeads.data.storage.palettestorage.PaletteStorageInterface
import com.lordgudzo.phototobeads.data.storage.palettestorage.models.BeadColorDto
import com.lordgudzo.phototobeads.domain.model.BeadColor
import com.lordgudzo.phototobeads.domain.repository.CreatePatternRepository

class CreatePatternImpl(
    private val imageStorage: CreatePatternStorageInterface,
    private val paletteStorage: PaletteStorageInterface

) : CreatePatternRepository {
    //Take Uri content// -> save to cache -> return Uri file//
    override fun saveImageToCash(uri: Uri): Uri? = imageStorage.saveTempImage(uri)


    override fun getImage(): Uri? = imageStorage.getTempImage()

    override fun createCroppedImageUri(): Uri {
        return imageStorage.createTempCroppedImageUri()
    }

    override fun saveCropResult(resultUri: Uri): Uri {
       return imageStorage.saveCropResult(resultUri)
    }

    override fun getBitMapWithSize(gridSize: Int): Bitmap {
        return imageStorage.getBitMapWithSize(gridSize)
    }

    override fun getPalette(palette: String): List<BeadColor> {
        return paletteStorage.getPalette(palette).toDomain()
    }
}