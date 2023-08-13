package com.colorata.wallman.core.ui.shapes

import androidx.annotation.FloatRange
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.colorata.animateaslifestyle.shapes.Angle
import com.colorata.animateaslifestyle.shapes.degrees
import com.colorata.animateaslifestyle.shapes.radians
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Most material shape(and not shape) path stored here.
 * Content:
 * - Scallop
 * - Clever
 * - Oval
 * - Wave
 * - Triangle
 * - Flower
 */
object MaterialPaths

/**
 * Wave path. This path calculates in ***rectangular*** coordinates system with this function:
 * ```
 * y = functionMultiplier * sin(x * waveMultiplier + animOffset) + yOffset
 * ```
 * @param size size of path
 * @param animOffset **X** offset of path in radians
 * @param waveMultiplier multiplier of given x
 * @param functionMultiplier multiplier of [sin]
 * @param scratch is function will be scratched
 * @param density density of shape. More density - more performance needed, more accurate will it be. Default - **`45.0`**
 */

fun MaterialPaths.wavePath(
    size: Size,
    animOffset: Float = 0f,
    offset: Offset = Offset.Zero,
    waveMultiplier: Float = 1f,
    functionMultiplier: Float = 1f,
    scratch: Boolean = true,
    density: Float = 45f
) =
    com.colorata.animateaslifestyle.shapes.rectangularBasedPath(
        size,
        scratch,
        density,
        offset = offset
    ) {
        sin(it * waveMultiplier + animOffset) * functionMultiplier
    }

/**
 * Scallop path.
 *
 * This path calculates in ***polar*** coordinates system with this function:
 * ```
 * r = offset + functionMultiplier * cos(degreeMultiplier * theta)
 * ```
 * ***NOTE:***
 *
 * [offset] + [functionMultiplier] should equal to ***1*** to fill *all* composable
 * @param size size of path
 * @param offset initial radius of function. Default - **`0.95`**
 * @param degreeMultiplier count of corners in shape. Can be used to create more complex shapes. Default - **`10`**
 * @param functionMultiplier multiplier of ***cos*** function. Default - **`0.05`**
 * @param scratch set is shape will scratch to all size or not. Default - **`false`**
 * @param density density of shape. More density - more performance needed, more accurate will it be. Default - **`45`**
 * @param rotation rotation of shape. Default - **`0.0 degrees`**
 * @param laps count of laps will be made. Default - **`1.0`**
 */

fun MaterialPaths.scallopPath(
    size: Size,
    offset: Float = 0.95f,
    degreeMultiplier: Float = 10f,
    @FloatRange(from = 0.0, fromInclusive = false) functionMultiplier: Float = 0.05f,
    scratch: Boolean = false,
    density: Float = 45f,
    rotation: Angle = degrees(0f),
    laps: Float = 1f
) = polarBasedPath(size, scratch, density, laps) {
    offset + functionMultiplier * cos(degreeMultiplier * (it))
}

/**
 * Clever path. Inherited from [scallopPath]
 * @param size size of path
 * @param scratch set is shape will scratch to all size or not. Default - **`false`**
 * @param density density of shape. More density - more performance needed, more accurate will it be. Default - **`45`**
 * @param rotation rotation of shape. Default - **`45.0 degrees`**
 * @param laps count of laps will be made. Default - **`1.0`**
 * @see scallopPath
 */

fun MaterialPaths.cleverPath(
    size: Size,
    offset: Float = 0.857f,
    degreeMultiplier: Float = 4f,
    functionMultiplier: Float = 0.143f,
    scratch: Boolean = false,
    density: Float = 45f,
    rotation: Angle = degrees(45f),
    laps: Float = 1f
) = scallopPath(
    size,
    offset = offset,
    degreeMultiplier = degreeMultiplier,
    functionMultiplier = functionMultiplier,
    scratch = scratch,
    density = density,
    rotation = rotation,
    laps = laps
)

/**
 * Flower path. Inherited from [scallopPath]
 *
 * ***NOTE:***
 *
 * Applying border to this shape can cause unexpected results.
 * @param size size of path
 * @param scratch set is shape will scratch to all size or not. Default - **`false`**
 * @param density density of shape. More density - more performance needed, more accurate will it be. Default - **`45`**
 * @param rotation rotation of shape. Default - **`0.0 degrees`**
 * @see scallopPath
 * @see cleverPath
 */

fun MaterialPaths.flowerPath(
    size: Size,
    offset: Float = 0.05f,
    degreeMultiplier: Float = 8 / 7f,
    functionMultiplier: Float = 0.95f,
    scratch: Boolean = false,
    density: Float = 45f,
    rotation: Angle = degrees(0f),
) = scallopPath(
    size,
    degreeMultiplier = degreeMultiplier,
    laps = 10f,
    offset = offset,
    functionMultiplier = functionMultiplier,
    rotation = rotation,
    density = density,
    scratch = scratch
)

/**
 * Oval path.
 * This path calculates in ***polar*** coordinates system with this function:
 * ```
 * r = (a * b) / √( (b * cos(theta))^2 + (a * sin(theta))^2 )
 * ```
 * ***NOTE:***
 *
 * [a] * [b] should equal to ***0.5*** to fill composable
 *
 * Also, [rotation] should not change too fast due to performance issues. Use `Modifier.rotate()` instead
 * @param size size of path
 * @param a horizontal multiplier. Default - **`0.5`**
 * @param b vertical multiplier. Default - **`1.0`**
 * @param density density of shape. More density - more performance needed, more accurate will it be. Default - **`90.0`**
 * @param rotation rotation of shape. Default - **`0.0 degrees`**
 * @param laps count of laps will be made. Default - **`1.0`**
 */

fun MaterialPaths.ovalPath(
    size: Size,
    a: Float = 0.5f,
    b: Float = 1f,
    density: Float = 90f,
    rotation: Angle = degrees(0f),
    laps: Float = 1f
) = polarBasedPath(size, false, density, laps) {
    (a * b) / (sqrt(
        (b * cos(it + rotation.radians)).pow(2) + (a * sin(it + rotation.radians)).pow(2)
    ))
}