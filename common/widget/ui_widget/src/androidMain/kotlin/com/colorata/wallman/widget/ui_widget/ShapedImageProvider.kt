package com.colorata.wallman.widget.ui_widget

import android.graphics.*
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.glance.LocalContext


class ShapedImageProvider(val shape: Bitmap, val image: Bitmap) {
    fun getBitmap(): Bitmap {
        val c = Canvas()
        val result = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)
        c.setBitmap(result)
        val paint = Paint()
        paint.isFilterBitmap = false
        c.drawBitmap(image, 0f, 0f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        c.drawBitmap(shape, 0f, 0f, paint)
        paint.xfermode = null
        return result
    }
}

@Composable
fun rememberShapedImageBitmap(@DrawableRes shape: Int, image: Bitmap): Bitmap {
    val context = LocalContext.current
    return remember {
        val width = image.width
        val height = image.height
        ShapedImageProvider(
            ContextCompat.getDrawable(context, shape)?.toBitmap(width = width, height = height)
                ?: Bitmap.createBitmap(0, 0, Bitmap.Config.ARGB_8888),
            image
        ).getBitmap()
    }
}
