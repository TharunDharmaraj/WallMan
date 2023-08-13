package com.colorata.wallman.core.ui.shapes

import android.graphics.Matrix
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import com.colorata.animateaslifestyle.material3.shapes.MaterialPaths
import com.colorata.animateaslifestyle.material3.shapes.scallopPath
import com.colorata.animateaslifestyle.shapes.Angle
import com.colorata.animateaslifestyle.shapes.degrees
import com.colorata.animateaslifestyle.shapes.polarCoordinates
import com.colorata.animateaslifestyle.shapes.radians
import com.colorata.animateaslifestyle.shapes.toRectangular
import kotlin.math.PI
import kotlin.math.min

/**
 * Creates [Path] for given ***polar*** coordinates system function
 * @param size size of path
 * @param scratch is function will be scratched in size. Default - **`false`**
 * @param density density of shape. More density - more performance needed, more accurate will it be. Default - **`45.0`**
 * @param laps count of laps will be made. Default - **`1.0`**
 * @param function ***polar*** coordinates system function. Theta(angle) in radians is given. Should return calculated radius
 * @see MaterialPaths.scallopPath
 */
fun polarBasedPath(
    size: Size,
    scratch: Boolean = false,
    density: Float = 45f,
    laps: Float = 1f,
    function: (theta: Float) -> Float?
): Path {
    val min = min(size.width, size.height)
    val w = if (scratch) size.width else min
    val h = if (scratch) size.height else min
    val xOffset = if (scratch || min == size.width) 0f else (size.width - size.height) / 2
    val yOffset = if (scratch || min == size.height) 0f else (size.height - size.width) / 2

    return Path().apply {
        var moveDone = false
        var angle = 0f
        val start = function(angle)?.let { polarCoordinates(it, radians(angle)).toRectangular() }
        if (start != null) {
            moveTo(start.x * w / 2 + w / 2 + xOffset, start.y * h / 2 + h / 2 + yOffset)
            moveDone = true
        }
        while (angle < laps * pi * 2 + pi / density) {
            val wp = function(angle)?.let { polarCoordinates(it, radians(angle)).toRectangular() }
            if (wp != null) {
                if (!moveDone) {
                    moveTo(wp.x * w / 2 + w / 2 + xOffset, wp.y * h / 2 + h / 2 + yOffset)
                    moveDone = true
                }
                lineTo(wp.x * w / 2 + w / 2 + xOffset, wp.y * h / 2 + h / 2 + yOffset)
            } else {
                moveDone = false
            }
            angle += pi / density
        }
    }
}

/**
 * Creates [Path]  for given **rectangular** coordinates system function
 * @param size size of function
 * @param scratch is function will be scratched in size. Default - **`true`**
 * @param density density of function. More density - more performance needed, more accurate will it be. Default - **`45.0`**
 * @param function ***rectangular*** coordinates system function. **X** is given. Should return calculated **Y**
 */
fun rectangularBasedPath(
    size: Size,
    scratch: Boolean = true,
    density: Float = 45f,
    offset: Offset = Offset.Zero,
    function: (theta: Float) -> Float
): Path {
    val min = min(size.width, size.height)
    val h = if (scratch) size.height else min
    val xOffset = if (scratch || min == size.width) 0f else (size.width - size.height) / 2
    val yOffset = if (scratch || min == size.height) 0f else (size.height - size.width) / 2
    return Path().apply {
        var x = 0f
        moveTo(x + xOffset + offset.x, function(x) * h / 2 + h / 2 + yOffset + offset.y)
        while (x * 20 /*magic number*/ <= size.width + 1 / density) {
            val wy = function(x)
            lineTo(x * 20 + xOffset + offset.x, wy * h / 2 + h / 2 + yOffset + offset.y)
            x += 1 / density
        }
    }
}

/**
 * Creates rectangle path for given [size]
 * @param size Size of rectangle
 * @param topStart Rounded corner in pixels for top left corner
 * @param topEnd Rounded corner in pixels for top right corner
 * @param bottomStart Rounded corner in pixels for bottom left corner
 * @param bottomEnd Rounded corner in pixels for bottom right corner
 * @param rotation Rotation of rectangle
 */
fun rectanglePath(
    size: Size,
    topStart: Float,
    topEnd: Float,
    bottomStart: Float,
    bottomEnd: Float,
    rotation: Angle
): Path {
    return Path().apply {
        addRoundRect(
            RoundRect(
                size.toRect(),
                topLeft = CornerRadius(topStart),
                topRight = CornerRadius(topEnd),
                bottomRight = CornerRadius(bottomEnd),
                bottomLeft = CornerRadius(bottomStart)
            )
        )
    }.rotate(rotation, size)
}

/**
 * Just Pi, but in [Float]
 */
internal const val pi = PI.toFloat()

/**
 * Rotates given path around center
 * @param angle Angle of rotation
 * @param size Size of path
 * @return Rotated path
 */
fun Path.rotate(angle: Angle, size: Size) = asAndroidPath().apply {
    transform(Matrix().apply {
        setRotate(angle.degrees, size.width / 2, size.height / 2)
    })
}.asComposePath()