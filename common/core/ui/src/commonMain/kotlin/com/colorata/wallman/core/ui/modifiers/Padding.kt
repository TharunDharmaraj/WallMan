@file:Suppress("UnusedReceiverParameter")

package com.colorata.wallman.core.ui.modifiers

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.offset
import com.colorata.wallman.core.ui.theme.LocalPaddings


object Padding


fun Modifier.navigationPadding() = composed {
    padding(Padding.navigationPadding())
}

@Composable
fun Padding.navigationPadding(): PaddingValues = LocalPaddings.current

fun Modifier.navigationStartPadding() = composed {
    padding(start = Padding.navigationStartPadding())
}

@Composable
fun Padding.navigationStartPadding(): Dp =
    LocalPaddings.current.calculateStartPadding(LocalLayoutDirection.current)


fun Modifier.navigationBottomPadding() = composed {
    padding(bottom = Padding.navigationBottomPadding())
}

@Composable
fun Padding.navigationBottomPadding(): Dp = LocalPaddings.current.calculateBottomPadding()

@Composable
fun Padding.navigationBarPadding(): Dp =
    WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

@Composable
fun Padding.statusBarPadding(): Dp =
    WindowInsets.statusBars.asPaddingValues().calculateTopPadding()


fun Modifier.withoutHorizontalPadding(horizontal: Dp) =
    withoutPadding(PaddingValues(horizontal = horizontal))


fun Modifier.withoutHorizontalPadding(start: Dp, end: Dp) =
    withoutPadding(PaddingValues(start = start, end = end))

fun Modifier.withoutPadding(padding: PaddingValues) = composed {
    val layoutDirection = LocalLayoutDirection.current
    layout { measurable, constraints ->
        val top = padding.calculateTopPadding().roundToPx()
        val bottom = padding.calculateBottomPadding().roundToPx()
        val start = padding.calculateStartPadding(layoutDirection).roundToPx()
        val end = padding.calculateEndPadding(layoutDirection).roundToPx()
        val placeable =
            measurable.measure(
                constraints.offset(
                    horizontal = start + end,
                    vertical = bottom + top
                )
            )
        layout(placeable.width - start - end, placeable.height - bottom - top) {
            placeable.place(-start, -bottom)
        }
    }
}