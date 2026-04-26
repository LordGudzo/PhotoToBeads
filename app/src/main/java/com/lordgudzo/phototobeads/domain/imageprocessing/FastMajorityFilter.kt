package com.lordgudzo.phototobeads.domain.imageprocessing

class FastMajorityFilter {
    fun apply(
        indices: IntArray,
        width: Int,
        height: Int,
        paletteSize: Int
    ): IntArray {
        val result = indices.copyOf()
        val counts = IntArray(paletteSize)
        for (y in 1 until height - 1) {
            for (x in 1 until width - 1) {
                val centerIdx = y * width + x
                counts.fill(0)
                for (dy in -1..1) for (dx in -1..1) counts[indices[(y + dy) * width + x + dx]]++
                var dominantColor = indices[centerIdx]
                var maxCount = counts[dominantColor]
                for (c in 0 until paletteSize) if (counts[c] > maxCount) {
                    dominantColor = c; maxCount = counts[c]
                }
                if (dominantColor != indices[centerIdx] && maxCount >= 5) result[centerIdx] =
                    dominantColor
            }
        }
        return result
    }
}