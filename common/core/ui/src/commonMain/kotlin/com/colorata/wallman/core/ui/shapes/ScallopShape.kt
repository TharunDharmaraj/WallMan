package com.colorata.wallman.core.ui.shapes

import androidx.annotation.FloatRange
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.colorata.animateaslifestyle.isCompositionLaunched
import com.colorata.animateaslifestyle.shapes.Angle
import com.colorata.animateaslifestyle.shapes.AnimatedShape
import com.colorata.animateaslifestyle.shapes.ExperimentalShapeApi
import com.colorata.animateaslifestyle.shapes.degrees
import com.colorata.animateaslifestyle.shapes.plus


/**
 * Scallop shape for any Composable.
 *
 * This shape calculates in ***polar*** coordinates system with this function:
 * ```
 * r = offset + functionMultiplier * cos(degreeMultiplier * theta)
 * ```
 * ***NOTE:***
 *
 * [offset] + [functionMultiplier] should equal to ***1*** to fill *all* composable
 * @param offset initial radius of function. Default - **`0.95`**
 * @param degreeMultiplier count of corners in shape. Can be used to create more complex shapes. Default - **`10`**
 * @param functionMultiplier multiplier of ***cos*** function. Default - **`0.05`**
 * @param scratch set is shape will scratch to all size or not. Default - **`false`**
 * @param density density of shape. More density - more performance needed, more accurate will it be. Default - **`45`**
 * @param rotation rotation of shape. Default - **`0.0 degrees`**
 * @param laps count of laps will be made. Default - **`1.0`**
 */
@OptIn(ExperimentalShapeApi::class)
class ScallopShape(
    private val offset: Float = 0.95f,
    private val degreeMultiplier: Float = 10f,
    @FloatRange(from = 0.0, fromInclusive = false) private val functionMultiplier: Float = 0.05f,
    private val scratch: Boolean = false,
    private val density: Float = 100f,
    private val rotation: Angle = degrees(0f),
    private val laps: Float = 1f
) : AnimatedShape {
    @Composable
    override fun animateToCircle(animationSpec: TweenSpec<Float>, rotation: Angle): AnimatedShape {
        val animatedOffset by animateFloatAsState(
            targetValue = if (isCompositionLaunched()) 1f else this.offset,
            animationSpec = animationSpec, label = ""
        )
        val animatedFunctionMultiplier by animateFloatAsState(
            targetValue = if (isCompositionLaunched()) 0f else this.functionMultiplier,
            animationSpec = animationSpec, label = ""
        )
        return ScallopShape(
            animatedOffset,
            this.degreeMultiplier,
            animatedFunctionMultiplier,
            this.scratch,
            this.density,
            this.rotation + rotation,
            this.laps
        )
    }

    @Composable
    override fun animateFromCircle(
        animationSpec: TweenSpec<Float>, rotation: Angle
    ): AnimatedShape {
        val animatedOffset by animateFloatAsState(
            targetValue = if (!isCompositionLaunched()) 1f else this.offset,
            animationSpec = animationSpec, label = ""
        )
        val animatedFunctionMultiplier by animateFloatAsState(
            targetValue = if (!isCompositionLaunched()) 0f else this.functionMultiplier,
            animationSpec = animationSpec, label = ""
        )
        return ScallopShape(
            animatedOffset,
            this.degreeMultiplier,
            animatedFunctionMultiplier,
            this.scratch,
            this.density,
            this.rotation + rotation,
            this.laps
        )
    }

    override fun createOutline(
        size: Size, layoutDirection: LayoutDirection, density: Density
    ): Outline = Outline.Generic(
        MaterialPaths.scallopPath(
            size,
            offset,
            degreeMultiplier,
            functionMultiplier,
            scratch,
            this.density,
            rotation,
            laps
        )
    )
}