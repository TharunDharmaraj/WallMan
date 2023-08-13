package com.colorata.wallman.core.ui.shapes

import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
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
 * Flower shape for any Composable. Inherited from [ScallopShape]
 *
 * ***NOTE:***
 *
 * Applying border to this shape can cause unexpected results.
 * @param scratch set is shape will scratch to all size or not. Default - **`false`**
 * @param density density of shape. More density - more performance needed, more accurate will it be. Default - **`45`**
 * @param rotation rotation of shape. Default - **`0.0 degrees`**
 * @see ScallopShape
 */
@OptIn(ExperimentalShapeApi::class)
class FlowerShape(
    private val scratch: Boolean = false,
    private val density: Float = 45f,
    private val rotation: Angle = degrees(0f)
) : AnimatedShape {
    private val offset: Float = 0.05f
    private val degreeMultiplier: Float = 8 / 7f
    private val functionMultiplier: Float = 0.95f
    private val laps: Float = 10f

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
        animationSpec: TweenSpec<Float>,
        rotation: Angle
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
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ) = Outline.Generic(
        MaterialPaths.flowerPath(
            size,
            offset,
            degreeMultiplier,
            functionMultiplier,
            scratch,
            this.density,
            rotation
        )
    )
}