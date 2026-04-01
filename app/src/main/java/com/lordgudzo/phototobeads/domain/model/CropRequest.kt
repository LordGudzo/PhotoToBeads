package com.lordgudzo.phototobeads.domain.model

import android.net.Uri

data class CropRequest(
    val inputUri: Uri,
    val outputUri: Uri,
    val aspectX: Float = 1f,
    val aspectY: Float = 1f,
    val maxWidth: Int = 1500,
    val maxHeight: Int = 1500,
    val quality: Int = 90
)
