package com.lordgudzo.phototobeads.ui.screen.createpatternscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lordgudzo.phototobeads.domain.model.BeadColor
import com.lordgudzo.phototobeads.domain.model.PatternResult

@Composable
fun PalettePanel(
    result: PatternResult,
    modifier: Modifier = Modifier
) {
    val beadCounts: Map<BeadColor, Int> = remember(result) {
        val counts = mutableMapOf<BeadColor, Int>()
        result.indices.forEach { colorIndex ->
            val color = result.palette[colorIndex]
            counts[color] = (counts[color] ?: 0) + 1
        }
        counts
    }

    val sortedPalette = remember(beadCounts) {
        result.palette.sortedByDescending { beadCounts[it] ?: 0 }
    }

    HorizontalDivider(thickness = 1.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Palette",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "${result.palette.size} colors",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    LazyColumn(modifier = modifier.padding(horizontal = 16.dp)) {
        items(sortedPalette) { beadColor ->
            PaletteRow(
                beadColor = beadColor,
                count = beadCounts[beadColor] ?: 0
            )
        }
    }
}

@Composable
private fun PaletteRow(
    beadColor: BeadColor,
    count: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    color = Color(beadColor.colorInt),
                    shape = RoundedCornerShape(4.dp)
                )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = beadColor.code,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(60.dp)
        )
        Text(
            text = beadColor.name,
            fontSize = 13.sp,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "$count шт",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}