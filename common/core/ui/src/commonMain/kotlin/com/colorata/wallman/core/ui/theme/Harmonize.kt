package com.colorata.wallman.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import dev.sasikanth.material.color.utilities.blend.Blend

@Composable
fun harmonize(color: Color): Color {
    val primary = MaterialTheme.colorScheme.primary
    return remember(color, primary) { Color(Blend.harmonize(color.toArgb(), primary.toArgb())) }
}