package com.colorata.wallman.core.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter

@Immutable
sealed interface Resource {
    enum class Drawable: Resource {
        LOGO,
        CIRCLE,
        CLEVER,
        SCALLOP,
        SQUARE
    }
}

@Composable
expect fun painterDrawable(resource: Resource.Drawable): Painter

@Composable
expect fun bitmapAsset(assetName: String): ImageBitmap

@Composable
expect fun cachedImageBitmap(path: String): ImageBitmap

@Composable
expect fun storagedImageBitmap(path: String): ImageBitmap