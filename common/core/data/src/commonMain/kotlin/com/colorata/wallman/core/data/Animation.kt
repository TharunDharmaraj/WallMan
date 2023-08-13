package com.colorata.wallman.core.data

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf

@Immutable
sealed interface AnimationSpec<T> {
    val emphasized: T
    val emphasizedDecelerate: T
    val emphasizedAccelerate: T
    val standard: T
    val standardDecelerate: T
    val standardAccelerate: T
}

data class EasingSpec(
    override val emphasized: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f),
    override val emphasizedDecelerate: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f),
    override val emphasizedAccelerate: Easing = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f),
    override val standard: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f),
    override val standardDecelerate: Easing = CubicBezierEasing(0f, 0f, 0f, 1f),
    override val standardAccelerate: Easing = CubicBezierEasing(0.3f, 0f, 1f, 1f)
) : AnimationSpec<Easing>

data class Animation(val durationSpec: DurationSpec, val easingSpec: EasingSpec) {
    fun <T> emphasized(
        durationMillis: Int = durationSpec.long2, delayMillis: Int = 0
    ): FiniteAnimationSpec<T> = tween(durationMillis, delayMillis, easingSpec.emphasized)

    fun <T> emphasizedDecelerate(
        durationMillis: Int = durationSpec.medium3, delayMillis: Int = 0
    ): FiniteAnimationSpec<T> = tween(durationMillis, delayMillis, easingSpec.emphasizedDecelerate)

    fun <T> emphasizedAccelerate(
        durationMillis: Int = durationSpec.medium4, delayMillis: Int = 0
    ): FiniteAnimationSpec<T> = tween(durationMillis, delayMillis, easingSpec.emphasizedAccelerate)

    fun <T> standard(
        durationMillis: Int = durationSpec.medium2, delayMillis: Int = 0
    ): FiniteAnimationSpec<T> = tween(durationMillis, delayMillis, easingSpec.standard)

    fun <T> standardDecelerate(
        durationMillis: Int = durationSpec.short3, delayMillis: Int = 0
    ): FiniteAnimationSpec<T> = tween(durationMillis, delayMillis, easingSpec.standardDecelerate)

    fun <T> standardAccelerate(
        durationMillis: Int = durationSpec.short4, delayMillis: Int = 0
    ): FiniteAnimationSpec<T> = tween(durationMillis, delayMillis, easingSpec.standardAccelerate)
}

data class DurationSpec(
    val short1: Int = 50,
    val short2: Int = 100,
    val short3: Int = 150,
    val short4: Int = 200,
    val medium1: Int = 250,
    val medium2: Int = 300,
    val medium3: Int = 350,
    val medium4: Int = 400,
    val long1: Int = 450,
    val long2: Int = 500,
    val long3: Int = 550,
    val long4: Int = 600,
    val extraLong1: Int = 700,
    val extraLong2: Int = 800,
    val extraLong3: Int = 900,
    val extraLong4: Int = 1000
)

val LocalAnimation = compositionLocalOf { Animation(DurationSpec(), EasingSpec()) }

val MaterialTheme.animation: Animation
    @Composable @ReadOnlyComposable get() = LocalAnimation.current