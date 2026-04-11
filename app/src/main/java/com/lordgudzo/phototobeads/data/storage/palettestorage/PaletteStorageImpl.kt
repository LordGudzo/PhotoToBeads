package com.lordgudzo.phototobeads.data.storage.palettestorage

import android.content.Context
import com.lordgudzo.phototobeads.data.storage.palettestorage.models.BeadColorDto
import com.lordgudzo.phototobeads.data.storage.palettestorage.models.PaletteResponse
import kotlinx.serialization.json.Json

class PaletteStorageImpl(private val context: Context): PaletteStorageInterface {
    override fun getPalette(palette: String): List<BeadColorDto> {
        return loadPalette(palette)
    }

    private fun loadPalette(palette: String): List<BeadColorDto> {
        val jsonString = context.assets
            .open("${palette}BeadsPalette.json")
            .bufferedReader()
            .use { it.readText() }

        val parsed = Json.decodeFromString<PaletteResponse>(jsonString)
        return parsed.fullBeadPalette
    }
}