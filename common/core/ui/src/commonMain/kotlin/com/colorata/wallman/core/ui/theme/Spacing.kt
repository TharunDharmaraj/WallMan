package com.colorata.wallman.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.colorata.animateaslifestyle.material3.isCompact
import com.colorata.wallman.core.ui.util.LocalWindowSizeConfiguration

data class Spacing(
    val default: Dp = 0.dp,
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 12.dp,
    val large: Dp = 16.dp,
    val extraLarge: Dp = 28.dp
)

val LocalSpacing = compositionLocalOf { Spacing() }

val MaterialTheme.spacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current

val Spacing.screenPadding: Dp
    @Composable
    @ReadOnlyComposable
    get() {
        val windowSize = LocalWindowSizeConfiguration.current
        return if (windowSize.isCompact()) large else extraLarge
    }