package com.colorata.wallman.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.WallpaperRotation: ImageVector
    get() {
        if (_wallpaperRotation != null) {
            return _wallpaperRotation!!
        }
        _wallpaperRotation = ImageVector.Builder(
            name = "WallpaperRotation",
            defaultWidth = 200.0.dp,
            defaultHeight = 200.0.dp,
            viewportWidth = 24.0F,
            viewportHeight = 24.0F,
        ).path(
            fill = SolidColor(Color(0xFFFFFFFF)),
            fillAlpha = 1.0F,
            strokeAlpha = 1.0F,
            strokeLineWidth = 0.0F,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0F,
            pathFillType = PathFillType.NonZero,
        ) {
            moveTo(14.0F, 9.0F)
            horizontalLineToRelative(-4.0F)
            curveToRelative(-1.1F, 0.0F, -2.0F, 0.9F, -2.0F, 2.0F)
            lineToRelative(0.0F, 4.0F)
            curveToRelative(0.0F, 1.1F, 0.9F, 2.0F, 2.0F, 2.0F)
            horizontalLineToRelative(4.0F)
            curveToRelative(1.1F, 0.0F, 2.0F, -0.9F, 2.0F, -2.0F)
            lineToRelative(0.0F, -4.0F)
            curveTo(16.0F, 9.9F, 15.1F, 9.0F, 14.0F, 9.0F)

            moveTo(9.0F, 15.0F)
            lineToRelative(1.5F, -1.92F)
            lineToRelative(1.0F, 1.31F)
            lineTo(13.0F, 12.6F)
            lineToRelative(2.0F, 2.4F)
            horizontalLineTo(9.0F)
            close()
        }.path(
            fill = SolidColor(Color(0xFFFFFFFF)),
            fillAlpha = 1.0F,
            strokeAlpha = 1.0F,
            strokeLineWidth = 0.0F,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0F,
            pathFillType = PathFillType.NonZero,
        ) {
            moveTo(11.5F, 7.5F)
            lineTo(15.0F, 4.0F)
            lineToRelative(-3.5F, -3.5F)
            lineToRelative(-1.41F, 1.42F)
            lineToRelative(1.09F, 1.09F)
            curveTo(11.12F, 3.01F, 11.06F, 3.0F, 11.0F, 3.0F)
            verticalLineToRelative(0.05F)
            curveTo(5.95F, 3.55F, 2.0F, 7.81F, 2.0F, 13.0F)
            curveToRelative(0.0F, 4.76F, 3.33F, 8.75F, 7.79F, 9.75F)
            lineToRelative(0.44F, -1.95F)
            curveTo(6.67F, 19.99F, 4.0F, 16.8F, 4.0F, 13.0F)
            curveToRelative(0.0F, -4.12F, 3.13F, -7.52F, 7.13F, -7.95F)
            lineToRelative(-1.04F, 1.04F)
            lineTo(11.5F, 7.5F)
            close()
        }.path(
            fill = SolidColor(Color(0xFFFFFFFF)),
            fillAlpha = 1.0F,
            strokeAlpha = 1.0F,
            strokeLineWidth = 0.0F,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0F,
            pathFillType = PathFillType.NonZero,
        ) {
            moveTo(13.79F, 20.8F)
            lineToRelative(0.45F, 1.95F)
            curveToRelative(1.45F, -0.33F, 2.84F, -1.0F, 4.01F, -1.93F)
            lineToRelative(-1.25F, -1.56F)
            curveTo(16.06F, 20.0F, 14.95F, 20.53F, 13.79F, 20.8F)
            close()
        }.path(
            fill = SolidColor(Color(0xFFFFFFFF)),
            fillAlpha = 1.0F,
            strokeAlpha = 1.0F,
            strokeLineWidth = 0.0F,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0F,
            pathFillType = PathFillType.NonZero,
        ) {
            moveTo(20.0F, 13.0F)
            curveToRelative(0.0F, 1.21F, -0.27F, 2.38F, -0.79F, 3.47F)
            lineToRelative(1.8F, 0.87F)
            curveTo(21.66F, 15.99F, 22.0F, 14.54F, 22.0F, 13.0F)
            horizontalLineTo(20.0F)
            close()
        }.path(
            fill = SolidColor(Color(0xFFFFFFFF)),
            fillAlpha = 1.0F,
            strokeAlpha = 1.0F,
            strokeLineWidth = 0.0F,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0F,
            pathFillType = PathFillType.NonZero,
        ) {
            moveTo(18.21F, 5.16F)
            lineToRelative(-1.24F, 1.57F)
            curveToRelative(0.94F, 0.74F, 1.71F, 1.7F, 2.23F, 2.78F)
            lineTo(21.0F, 8.63F)
            curveTo(20.34F, 7.29F, 19.38F, 6.09F, 18.21F, 5.16F)
            close()
        }.build()
        return _wallpaperRotation!!
    }
private var _wallpaperRotation: ImageVector? = null