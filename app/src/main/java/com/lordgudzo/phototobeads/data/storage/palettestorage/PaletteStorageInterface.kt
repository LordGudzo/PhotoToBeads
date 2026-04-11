package com.lordgudzo.phototobeads.data.storage.palettestorage

import com.lordgudzo.phototobeads.data.storage.palettestorage.models.BeadColorDto

interface PaletteStorageInterface {
    fun getPalette(palette: String): List<BeadColorDto>
}