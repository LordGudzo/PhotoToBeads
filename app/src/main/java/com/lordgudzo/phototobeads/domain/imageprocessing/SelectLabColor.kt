package com.lordgudzo.phototobeads.domain.imageprocessing

import com.lordgudzo.phototobeads.domain.model.BeadColor
import com.lordgudzo.phototobeads.domain.model.LabPoint

class SelectLabColor {
    /**
     * Matches each center to a unique color from the palette.
     * Returns exactly as many colors as there are centers (no duplicates).
     *
     * @param centers List of cluster centers (size = colorCount)
     * @param palette List of all available bead colors
     * @param paletteLab List of LabPoints for each bead color (same order as palette)
     * @return List of unique bead colors (size = colorCount)
     */
    fun apply(
        centers: List<LabPoint>,
        palette: List<BeadColor>,
        paletteLab: List<LabPoint>
    ): List<BeadColor> {
        if (centers.isEmpty()) return emptyList()

        // Build list of all possible (centerIndex, paletteIndex, distance)
        val pairs = mutableListOf<Triple<Int, Int, Double>>()
        for (cIdx in centers.indices) {
            for (pIdx in paletteLab.indices) {
                val dist = centers[cIdx].checkLabPointDistance(paletteLab[pIdx])
                pairs.add(Triple(cIdx, pIdx, dist))
            }
        }

        // Sort by distance (closest first)
        pairs.sortBy { it.third }

        val usedCenters = BooleanArray(centers.size)
        val usedPalette = BooleanArray(palette.size)
        val assignment = IntArray(centers.size) { -1 } // which palette index assigned to each center

        for ((cIdx, pIdx, _) in pairs) {
            if (!usedCenters[cIdx] && !usedPalette[pIdx]) {
                usedCenters[cIdx] = true
                usedPalette[pIdx] = true
                assignment[cIdx] = pIdx
            }
        }

        // Should never happen if centers.size <= palette.size, but just in case:
        for (cIdx in centers.indices) {
            if (assignment[cIdx] == -1) {
                // assign any unused palette color (fallback)
                val freePalette = (0 until palette.size).firstOrNull { !usedPalette[it] }
                if (freePalette != null) {
                    assignment[cIdx] = freePalette
                    usedPalette[freePalette] = true
                } else {
                    // extremely rare: just use the closest (duplicate allowed)
                    val closest = paletteLab.indices.minByOrNull { centers[cIdx].checkLabPointDistance(paletteLab[it]) }!!
                    assignment[cIdx] = closest
                }
            }
        }

        return assignment.map { palette[it] }
    }
}