package com.lordgudzo.phototobeads.ui.screen.createpatternscreen


import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import com.lordgudzo.phototobeads.domain.imageprocessing.PatternGridRenderer
import com.lordgudzo.phototobeads.domain.model.PatternResult
import androidx.compose.foundation.Canvas


@Composable
fun ZoomablePatternCanvas(
    result: PatternResult,
    modifier: Modifier = Modifier
) {
    // Renderer — created once per PatternResult
    val renderer = remember(result) { PatternGridRenderer(result) }

    // Zoom level: 1f = 1 cell = cellSize px, max zoom = 10x, min = fit whole grid
    var scale by remember { mutableFloatStateOf(1f) }

    // Pan offset in screen pixels
    var offset by remember { mutableStateOf(Offset.Zero) }

    // Screen size — needed to calculate min zoom
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = modifier
            .clipToBounds()
            .onSizeChanged { size ->
                canvasSize = size
                // On first layout — fit grid to screen
                if (scale == 1f && size.width > 0) {
                    scale = (size.width.toFloat() / renderer.totalWidth)
                        .coerceAtMost(1f)
                }
            }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->

                    // Update zoom
                    val minScale = if (canvasSize.width > 0) {
                        (canvasSize.width / renderer.totalWidth).coerceAtMost(0.1f)
                    } else 0.1f

                    scale = (scale * zoom).coerceIn(minScale, 10f)

                    // Update pan — clamp so user can't pan outside grid
                    val maxOffsetX = 0f
                    val minOffsetX = (canvasSize.width - renderer.totalWidth * scale)
                        .coerceAtMost(0f)
                    val maxOffsetY = 0f
                    val minOffsetY = (canvasSize.height - renderer.totalHeight * scale)
                        .coerceAtMost(0f)

                    offset = Offset(
                        x = (offset.x + pan.x).coerceIn(minOffsetX, maxOffsetX),
                        y = (offset.y + pan.y).coerceIn(minOffsetY, maxOffsetY)
                    )
                }
            }
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawIntoCanvas { composeCanvas ->
                val androidCanvas = composeCanvas.nativeCanvas

                // Apply zoom and pan transforms
                androidCanvas.save()
                androidCanvas.translate(offset.x, offset.y)
                androidCanvas.scale(scale, scale)

                // Calculate viewport in grid coordinates
                // What part of the grid is currently visible?
                val viewportLeft = -offset.x / scale
                val viewportTop = -offset.y / scale
                val viewportRight = (canvasSize.width - offset.x) / scale
                val viewportBottom = (canvasSize.height - offset.y) / scale

                renderer.draw(
                    canvas = androidCanvas,
                    viewportLeft = viewportLeft,
                    viewportTop = viewportTop,
                    viewportRight = viewportRight,
                    viewportBottom = viewportBottom
                )

                androidCanvas.restore()
            }
        }
    }
}