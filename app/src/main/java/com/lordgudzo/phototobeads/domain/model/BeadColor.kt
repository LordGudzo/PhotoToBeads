package com.lordgudzo.phototobeads.domain.model

import android.graphics.Color
import androidx.core.graphics.ColorUtils

data class BeadColor(
    val code: String,
    val name: String,
    val r: Int,
    val g: Int,
    val b: Int
) {
    val colorInt: Int
        get() = Color.rgb(r, g, b)
    fun toLabPoint(): LabPoint {
        val lab = DoubleArray(3)
        ColorUtils.RGBToLAB(r, g, b, lab)
        return LabPoint(lab[0], lab[1], lab[2])
    }
}
