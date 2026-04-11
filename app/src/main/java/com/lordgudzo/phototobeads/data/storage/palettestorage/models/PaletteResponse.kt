package com.lordgudzo.phototobeads.data.storage.palettestorage.models

import kotlinx.serialization.Serializable


@Serializable
data class PaletteResponse(
    val fullBeadPalette: List<BeadColorDto>
)
