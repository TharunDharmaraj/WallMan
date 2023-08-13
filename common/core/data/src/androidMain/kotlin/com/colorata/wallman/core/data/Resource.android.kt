package com.colorata.wallman.core.data

import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

@Composable
actual fun painterDrawable(resource: Resource.Drawable): Painter {
    val id = remember(resource) { mapResourceToId(resource) }
    return painterResource(id)
}

private fun mapResourceToId(resource: Resource.Drawable): Int =
    // TODO: Add actual mapping
    when (resource) {
        Resource.Drawable.CLEVER -> androidx.core.R.drawable.ic_call_answer
        Resource.Drawable.CIRCLE -> androidx.core.R.drawable.ic_call_answer
        Resource.Drawable.LOGO -> androidx.core.R.drawable.ic_call_answer
        Resource.Drawable.SCALLOP -> androidx.core.R.drawable.ic_call_answer
        Resource.Drawable.SQUARE -> androidx.core.R.drawable.ic_call_answer
    }


@Composable
actual fun bitmapAsset(assetName: String): ImageBitmap {
    val context = LocalContext.current
    val bitmap = remember(assetName) {
        val fullPath =
            context.assets.list("")?.find { assetName in it.split(".", "/") }
                ?: error("Asset '$assetName' not found")

        context.assets.open(fullPath)
            .use { BitmapFactory.decodeStream(it) }
            .asImageBitmap()
    }
    return bitmap
}

@Composable
actual fun cachedImageBitmap(path: String): ImageBitmap {
    val context = LocalContext.current
    return imageBitmapFromPath {
        context.cacheDir.absolutePath + "/$path".replace("//", "/")
    }
}

@Composable
actual fun storagedImageBitmap(path: String): ImageBitmap {
    val context = LocalContext.current
    return imageBitmapFromPath {
        context.filesDir.absolutePath + "/$path".replace("//", "/")
    }
}

private val cache = mutableStateMapOf<String, ImageBitmap>()
@Composable
private fun imageBitmapFromPath(path: () -> String): ImageBitmap {
    val bitmap = remember(path) {
        println(path())
        cache.getOrPut(path()) {
            BitmapFactory.decodeFile(path()).asImageBitmap()
        }
    }
    return bitmap
}