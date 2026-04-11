package com.lordgudzo.phototobeads.domain.model

data class PatternResult(
    val width: Int,
    val height: Int,
    val indices: IntArray,
    val palette: List<ThreadColor>,
    val symbolMap: Map<Int, String>
)

