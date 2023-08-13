package com.colorata.wallman.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.colorata.wallman.core.data.memoizeHash
import com.colorata.wallman.core.data.storagedImageBitmap
import dev.sasikanth.material.color.utilities.hct.Hct
import dev.sasikanth.material.color.utilities.quantize.QuantizerCelebi
import dev.sasikanth.material.color.utilities.scheme.Scheme

fun Scheme.toColorScheme(): ColorScheme {
    return darkColorScheme(
        primary = Color(primary),
        onPrimary = Color(onPrimary),
        primaryContainer = Color(primaryContainer),
        onPrimaryContainer = Color(onPrimaryContainer),
        inversePrimary = Color(inversePrimary),
        secondary = Color(secondary),
        onSecondary = Color(onSecondary),
        secondaryContainer = Color(secondaryContainer),
        onSecondaryContainer = Color(onSecondaryContainer),
        tertiary = Color(tertiary),
        onTertiary = Color(onTertiary),
        tertiaryContainer = Color(tertiaryContainer),
        onTertiaryContainer = Color(onTertiaryContainer),
        background = Color(background),
        onBackground = Color(onBackground),
        surface = Color(surface),
        onSurface = Color(onSurface),
        surfaceVariant = Color(surfaceVariant),
        onSurfaceVariant = Color(onSurfaceVariant),
        inverseSurface = Color(inverseSurface),
        inverseOnSurface = Color(inverseOnSurface),
        error = Color(error),
        onError = Color(onError),
        errorContainer = Color(errorContainer),
        onErrorContainer = Color(onErrorContainer),
        outline = Color(outline),
        outlineVariant = Color(outlineVariant),
        scrim = Color(scrim)
    )
}

/**
 * Uses Material Color Utilities api to find dominant color.
 * @param chroma chroma of generated color
 */
private fun ImageBitmap.overallDominantColor(chroma: Int = 30): Int {
    val buffer = IntArray(width * height + 1)
    readPixels(buffer)
    asAndroidBitmap().getPixels(buffer, 0, width, 0, 0, width - 1, height - 1)
    return QuantizerCelebi.quantize(buffer, maxColors = 1).toList()[0].first.let {
        Hct
            .fromInt(it)
            .apply { setChroma(chroma.toDouble()) }
            .toInt()
    }
}

fun ImageBitmap.extractColorScheme(darkTheme: Boolean): ColorScheme {
    val dominant = overallDominantColor()
    val scheme = if (darkTheme) Scheme.darkContent(dominant) else Scheme.lightContent(dominant)
    return scheme.toColorScheme()
}

private val extractedColors =
    memoizeHash({ assetName: String, pair -> assetName to pair.second }) { _: String, pair: Pair<ImageBitmap, Boolean> ->
        val (bitmap, darkTheme) = pair
        bitmap.extractColorScheme(darkTheme)
    }

@Composable
fun WallManContentTheme(
    assetName: String,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val image = storagedImageBitmap(assetName)
    var colorScheme by remember { mutableStateOf<ColorScheme?>(null) }
    LaunchedEffect(assetName, darkTheme) {
        colorScheme = extractedColors(assetName, image to darkTheme)
    }

    WallManPreviewTheme(darkTheme = darkTheme, colorScheme = colorScheme) {
        content()
    }
}