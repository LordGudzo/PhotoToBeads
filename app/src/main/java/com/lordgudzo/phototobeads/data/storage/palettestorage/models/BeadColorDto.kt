package com.lordgudzo.phototobeads.data.storage.palettestorage.models

import kotlinx.serialization.Serializable

@Serializable
data class BeadColorDto (
    val code: String,
    val name: String,
    val r: Int,
    val g: Int,
    val b: Int
)


