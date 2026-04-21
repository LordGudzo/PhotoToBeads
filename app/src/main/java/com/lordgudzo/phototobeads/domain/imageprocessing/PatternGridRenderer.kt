package com.lordgudzo.phototobeads.domain.imageprocessing

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.lordgudzo.phototobeads.domain.model.PatternResult

/**
 * Draws bead pattern grid on any Canvas.
 * Works for both screen rendering and PNG export.
 *
 * Each cell looks like:
 * ●─────●
 * │     │  ← bead color fill
 * ●─────●
 *
 * Every 10 cells — thicker grid line (standard bead pattern).
 */
class PatternGridRenderer(
    private val result: PatternResult,
    private val cellSize: Float = 32f  // ← параметр
) {

    // Paints — created once, reused for every cell
    private val fillPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = false // sharp edges for bead cells
    }

    private val thinLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.argb(80, 0, 0, 0) // semi-transparent black
        strokeWidth = 0.5f
        isAntiAlias = false
    }

    private val thickLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.argb(160, 0, 0, 0)
        strokeWidth = 1.5f
        isAntiAlias = false
    }

    private val dotPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.argb(120, 80, 80, 80)
        isAntiAlias = true
    }

    /**
     * Total grid size in pixels — used by screen to set scroll bounds.
     */
    val totalWidth: Float get() = result.width * cellSize
    val totalHeight: Float get() = result.height * cellSize

    /**
     * Draw visible part of the grid.
     *
     * @param canvas — where to draw
     * @param viewportLeft/Top/Right/Bottom — visible area in grid coordinates
     *        (already accounts for zoom and pan)
     *
     * Culling: we calculate which cells are visible
     * and draw ONLY those. Skip invisible cells completely.
     */
    fun draw(
        canvas: Canvas,
        viewportLeft: Float,
        viewportTop: Float,
        viewportRight: Float,
        viewportBottom: Float
    ) {
        // Calculate which cells are visible
        val firstCol = (viewportLeft / cellSize).toInt().coerceIn(0, result.width - 1)
        val lastCol = (viewportRight / cellSize).toInt().coerceIn(0, result.width - 1)
        val firstRow = (viewportTop / cellSize).toInt().coerceIn(0, result.height - 1)
        val lastRow = (viewportBottom / cellSize).toInt().coerceIn(0, result.height - 1)

        // Draw only visible cells
        for (row in firstRow..lastRow) {
            for (col in firstCol..lastCol) {
                drawCell(canvas, row, col)
            }
        }

        // Draw grid lines on top of cells
        drawGridLines(canvas, firstCol, lastCol, firstRow, lastRow)

        // Draw corner dots on top of grid lines
        drawCornerDots(canvas, firstCol, lastCol, firstRow, lastRow)
    }

    /**
     * Draw single bead cell — just a colored rectangle.
     */
    private fun drawCell(canvas: Canvas, row: Int, col: Int) {
        val left = col * cellSize
        val top = row * cellSize
        val right = left + cellSize
        val bottom = top + cellSize

        val index = row * result.width + col
        val colorIndex = result.indices[index]
        val beadColor = result.palette[colorIndex]

        fillPaint.color = beadColor.colorInt
        canvas.drawRect(left, top, right, bottom, fillPaint)
    }

    /**
     * Draw grid lines.
     * Thin line every cell.
     * Thick line every 10 cells — helps user count beads.
     */
    private fun drawGridLines(
        canvas: Canvas,
        firstCol: Int,
        lastCol: Int,
        firstRow: Int,
        lastRow: Int
    ) {
        // Vertical lines
        for (col in firstCol..lastCol + 1) {
            val x = col * cellSize
            val paint = if (col % 10 == 0) thickLinePaint else thinLinePaint
            canvas.drawLine(
                x, firstRow * cellSize,
                x, (lastRow + 1) * cellSize,
                paint
            )
        }

        // Horizontal lines
        for (row in firstRow..lastRow + 1) {
            val y = row * cellSize
            val paint = if (row % 10 == 0) thickLinePaint else thinLinePaint
            canvas.drawLine(
                firstCol * cellSize, y,
                (lastCol + 1) * cellSize, y,
                paint
            )
        }
    }

    /**
     * Draw small dots at cell corners — needle holes in real bead pattern.
     * Dot radius depends on cell size — looks good at any zoom.
     */
    private fun drawCornerDots(
        canvas: Canvas,
        firstCol: Int,
        lastCol: Int,
        firstRow: Int,
        lastRow: Int
    ) {
        val dotRadius = (cellSize * 0.07f).coerceIn(1f, 4f)

        // Draw dot at each corner intersection
        // Corner = where grid lines cross = (col * cellSize, row * cellSize)
        for (row in firstRow..lastRow + 1) {
            for (col in firstCol..lastCol + 1) {
                canvas.drawCircle(
                    col * cellSize,
                    row * cellSize,
                    dotRadius,
                    dotPaint
                )
            }
        }
    }

    /**
     * Draw full grid without culling — for PNG/PDF export.
     * Draws everything from (0,0) to (totalWidth, totalHeight).
     */
    fun drawFull(canvas: Canvas) {
        draw(
            canvas,
            viewportLeft = 0f,
            viewportTop = 0f,
            viewportRight = totalWidth,
            viewportBottom = totalHeight
        )
    }

    fun renderToBitmap(cellSizePx: Int = 10): Bitmap {
        // Создаём рендерер с маленьким cellSize специально для bitmap
        val exportRenderer = PatternGridRenderer(result, cellSize = cellSizePx.toFloat())
        val bitmap = Bitmap.createBitmap(
            result.width * cellSizePx,
            result.height * cellSizePx,
            Bitmap.Config.ARGB_8888
        )
        val canvas = android.graphics.Canvas(bitmap)
        exportRenderer.drawFull(canvas)
        return bitmap
    }
}