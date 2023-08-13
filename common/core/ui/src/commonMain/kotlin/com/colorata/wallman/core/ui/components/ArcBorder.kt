package com.colorata.wallman.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import com.colorata.animateaslifestyle.shapes.Arc
import com.colorata.animateaslifestyle.shapes.drawMaskArc
import com.colorata.animateaslifestyle.shapes.drawOffscreen

@Composable
fun ArcBorder(
    arc: Arc,
    border: BorderStroke,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    content: @Composable () -> Unit
) {
    val layoutDirection = LocalLayoutDirection.current
    val density = LocalDensity.current
    Box(
        modifier = modifier
            .clip(shape)
            .height(IntrinsicSize.Min)
            .width(IntrinsicSize.Min)
    ) {
        content()
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .drawOffscreen()
        ) {
            drawOutline(
                shape.createOutline(size, layoutDirection, density),
                brush = border.brush,
                style = Stroke(border.width.toPx(), cap = StrokeCap.Round)
            )
            drawMaskArc(arc, border.brush)
        }
    }
}