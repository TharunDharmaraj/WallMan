package com.colorata.wallman.core.ui.util

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

@Deprecated(
    "Please use LocalWindowSizeConfiguration.current",
    replaceWith = ReplaceWith(
        "LocalWindowSizeConfiguration.current",
        "com.colorata.wallman.core.ui.util.LocalWindowSizeConfiguration"
    )
)
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberWindowSize() =
    with(LocalConfiguration.current) {
        remember(screenWidthDp, screenHeightDp) {
            WindowSizeClass.calculateFromSize(
                DpSize(screenWidthDp.dp, screenHeightDp.dp)
            )
        }
    }

val LocalWindowSizeConfiguration =
    compositionLocalOf<WindowSizeClass> { error("No windowSize is provided") }

@Composable
fun ProvideWindowSize(
    windowSize: WindowSizeClass = rememberWindowSize(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalWindowSizeConfiguration provides windowSize) {
        content()
    }
}