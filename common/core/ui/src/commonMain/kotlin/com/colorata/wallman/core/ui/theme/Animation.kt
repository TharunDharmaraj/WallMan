package com.colorata.wallman.core.ui.theme

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.colorata.animateaslifestyle.fade
import com.colorata.animateaslifestyle.scale
import com.colorata.animateaslifestyle.slideHorizontally
import com.colorata.animateaslifestyle.slideVertically
import com.colorata.wallman.core.data.Animation

private const val INITIAL_SCALE = 0.98f

@Composable
fun Animation.emphasizedVerticalSlide(from: Float = 100f) = remember {
    fade(animationSpec = emphasized()) +
            slideVertically(
                from, animationSpec = emphasized()
            ) + scale(from = INITIAL_SCALE, animationSpec = emphasized())
}

context(AnimatedContentTransitionScope<*>)
fun Animation.emphasizedVerticalSlideContent(up: Boolean) =
    fadeIn(emphasized()) +
            slideInVertically { if (up) it else -it } +
            scaleIn(emphasized(), initialScale = INITIAL_SCALE) togetherWith
            fadeOut(emphasized()) +
            slideOutVertically { if (up) -it else it } +
            scaleOut(emphasized(), targetScale = INITIAL_SCALE) using
            SizeTransform(clip = false)

@Composable
fun Animation.emphasizedHorizontalSlide(from: Float = -100f) = remember {
    fade(animationSpec = emphasized()) +
            slideHorizontally(
                from, animationSpec = emphasized()
            )
}

fun Animation.emphasizedFade() = fade(animationSpec = emphasized())