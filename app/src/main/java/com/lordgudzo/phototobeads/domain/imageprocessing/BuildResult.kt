package com.lordgudzo.phototobeads.domain.imageprocessing

import com.lordgudzo.phototobeads.domain.model.BeadColor
import com.lordgudzo.phototobeads.domain.model.PatternResult

class BuildResult {

    /** Build PatternResult with symbols */
    fun apply(
        indices: IntArray,
        size: Int,
        selectedThreads: List<BeadColor>
    ): PatternResult {
        val symbolMap = generateDistinctSymbols(selectedThreads.size)
        return PatternResult(
            width = size,
            height = size,
            indices = indices,
            palette = selectedThreads,
            symbolMap = selectedThreads.indices.associateWith { symbolMap[it] }
        )
    }

    /** Generate distinct symbols for threads */
    private fun generateDistinctSymbols(count: Int): List<String> {
        val base = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        val extras =
            listOf("■", "□", "▲", "△", "●", "○", "♥", "♦", "♣", "♠", "★", "☆", "☀", "☁", "☂")
        val all = base.map { it.toString() } + extras
        val result = mutableListOf<String>()
        result.addAll(all.take(count))
        while (result.size < count) result.add("X${result.size}")
        return result
    }
}