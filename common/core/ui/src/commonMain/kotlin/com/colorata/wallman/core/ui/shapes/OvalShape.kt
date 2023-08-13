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
 * Oval shape for any Composable.
 * This shape calculates in ***polar*** coordinates system with this function:
 * ```
 * r = (a * b) / √( (b * cos(theta))^2 + (a * sin(theta))^2 )
 * ```
 * ***NOTE:***
 *
 * [a] * [b] should equal to ***0.5*** to fill composable
 *
 * Also, [rotation] should not change too fast due to performance issues. Use `Modifier.rotate()` instead
 * @param a horizontal multiplier. Default - **`0.5`**
 * @param b vertical multiplier. Default - **`1.0`**
 * @param density density of shape. More density - more performance needed, more accurate will it be. Default - **`90.0`**
 * @param rotation rotation of shape in degrees. Default - **`0.0`**
 * @param laps count of laps will be made. Default - **`1.0`**
 */
@OptIn(ExperimentalShapeApi::class)
class OvalShape(
    private val a: Float = 0.5f,
    private val b: Float = 1f,
    private val density: Float = 90f,
    private val rotation: Angle = degrees(0f),
    private val laps: Float = 1f
) : AnimatedShape {
    @Composable
    override fun animateToCircle(animationSpec: TweenSpec<Float>, rotation: Angle): AnimatedShape {
        val animatedA by animateFloatAsState(
            targetValue = if (isCompositionLaunched()) 1f else this.a,
            animationSpec = animationSpec, label = ""
        )
        val animatedB by animateFloatAsState(
            targetValue = if (isCompositionLaunched()) 1f else this.b,
            animationSpec = animationSpec, label = ""
        )
        return OvalShape(
            animatedA,
            animatedB,
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
        val animatedA by animateFloatAsState(
            targetValue = if (!isCompositionLaunched()) 1f else this.a,
            animationSpec = animationSpec, label = ""
        )
        val animatedB by animateFloatAsState(
            targetValue = if (!isCompositionLaunched()) 1f else this.b,
            animationSpec = animationSpec, label = ""
        )
        return OvalShape(
            animatedA,
            animatedB,
            this.density,
            this.rotation + rotation,
            this.laps
        )
    }

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ) = Outline.Generic(MaterialPaths.ovalPath(size, a, b, this.density, rotation, laps))

}