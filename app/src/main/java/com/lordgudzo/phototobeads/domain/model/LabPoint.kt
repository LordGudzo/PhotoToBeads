package com.lordgudzo.phototobeads.domain.model

data class LabPoint(var l: Double, var a: Double, var b: Double) {
    /**
     * This function calculates distance between two colors (points) in LAB space.
     *
     * Example:
     * Point 1: L=50, A=20, B=30
     * Point 2: L=60, A=10, B=40
     *
     * Step:
     * dl = 50 - 60 = -10
     * da = 20 - 10 = 10
     * db = 30 - 40 = -10
     *
     * Square:
     * dl*dl = 100
     * da*da = 100
     * db*db = 100
     *
     * Result = 100 + 100 + 100 = 300
     * */
    fun checkLabPointDistance(other: LabPoint): Double {
        val dl = l - other.l
        val da = a - other.a
        val db = b - other.b

        return dl * dl + da * da + db * db
    }

    fun copy() = LabPoint(l, a, b)

    fun clamp() { l = l.coerceIn(0.0, 100.0); a = a.coerceIn(-128.0, 127.0); b = b.coerceIn(-128.0, 127.0) }
}
