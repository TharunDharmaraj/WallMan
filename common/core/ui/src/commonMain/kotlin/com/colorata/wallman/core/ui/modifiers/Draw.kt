package com.colorata.wallman.core.ui.modifiers

import android.os.Build
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas

fun Modifier.drawWithMask(block: DrawScope.() -> Unit) =
    this then drawWithContent {
        // Needed because mask does not work properly in Q
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.Q) {
            with(drawContext.canvas.nativeCanvas) {
                val checkPoint = saveLayer(null, null)
                drawContent()
                block()
                restoreToCount(checkPoint)
            }
        } else {
            drawContent()
        }
    }