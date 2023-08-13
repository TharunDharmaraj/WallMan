package com.colorata.wallman.core.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import com.colorata.animateaslifestyle.material3.indicator.indeterminateArc
import com.colorata.animateaslifestyle.material3.isCompact
import com.colorata.animateaslifestyle.shapes.arc
import com.colorata.animateaslifestyle.shapes.degrees
import com.colorata.wallman.core.data.DownloadState
import com.colorata.wallman.core.data.animation
import com.colorata.wallman.core.data.displayedText
import com.colorata.wallman.core.data.rememberString
import com.colorata.wallman.core.ui.R
import com.colorata.wallman.core.ui.animation.animateVisibility
import com.colorata.wallman.core.ui.list.VisibilityList
import com.colorata.wallman.core.ui.list.animatedAtLaunch
import com.colorata.wallman.core.ui.list.rememberVisibilityList
import com.colorata.wallman.core.ui.modifiers.detectRotation
import com.colorata.wallman.core.ui.modifiers.displayRotation
import com.colorata.wallman.core.ui.modifiers.rememberRotationState
import com.colorata.wallman.core.ui.shapes.ScallopShape
import com.colorata.wallman.core.ui.theme.emphasizedVerticalSlide
import com.colorata.wallman.core.ui.theme.spacing
import com.colorata.wallman.core.ui.util.LocalWindowSizeConfiguration
import com.colorata.wallman.core.ui.util.animateObfuscated
import com.colorata.wallman.core.ui.util.animatedDots
import com.colorata.wallman.core.ui.util.shimmer.shimmer

@Composable
fun FullScreenLoadingIndicator(state: DownloadState, modifier: Modifier = Modifier) {
    val visibilityList = rememberVisibilityList(1) { }.animatedAtLaunch()
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LogoWithLabel(visibilityList.visible[0], state)
    }
}

@Composable
fun FullScreenShimmer(modifier: Modifier = Modifier) {
    Box(modifier.shimmer(visible = true).fillMaxSize())
}

@Composable
fun FullScreenLoadingIndicator(
    state: DownloadState,
    modifier: Modifier = Modifier,
    interactionSlot: @Composable BoxScope.(visible: Boolean) -> Unit
) {
    val visibilityList = rememberVisibilityList(2) { }.animatedAtLaunch()
    val windowSize = LocalWindowSizeConfiguration.current
    val buttons = @Composable { buttonsModifier: Modifier ->
        Box(buttonsModifier, contentAlignment = Alignment.Center) {
            interactionSlot(visibilityList.visible[1])
        }
    }
    if (windowSize.isCompact()) {
        CompactLayout(visibilityList, state, buttons, modifier)
    } else {
        ExpandedLayout(visibilityList, state, buttons, modifier)
    }
}

@Composable
private fun ExpandedLayout(
    visibilityList: VisibilityList<Unit>,
    state: DownloadState,
    buttons: @Composable (Modifier) -> Unit,
    modifier: Modifier
) {
    Row(
        modifier
            .fillMaxSize()
            .systemBarsPadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
    ) {
        LogoWithLabel(
            visibilityList.visible[0],
            state,
            Modifier
                .fillMaxHeight(0.8f)
                .weight(1f)
        )
        buttons(
            Modifier
                .weight(1f)
                .fillMaxHeight()
        )
    }
}

@Composable
private fun CompactLayout(
    visibilityList: VisibilityList<Unit>,
    state: DownloadState,
    buttons: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .systemBarsPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
    ) {
        LogoWithLabel(
            visibilityList.visible[0],
            state,
            Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        buttons(
            Modifier
                .weight(1f)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun LogoWithLabel(
    visible: Boolean,
    downloadState: DownloadState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier.animateVisibility(
            visible,
            MaterialTheme.animation.emphasizedVerticalSlide()
        ),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Logo(
            progress = {
                when (downloadState) {
                    is DownloadState.Downloading -> downloadState.progress * 100f
                    else -> 100f
                }
            },
            isIndeterminate = downloadState is DownloadState.Connecting,
            modifier = Modifier
                .weight(1f)
                .zIndex(3f)
        )
        val dots = animatedDots()
        androidx.compose.material3.Text(
            animateObfuscated(
                rememberString(
                    downloadState.displayedText()
                )
            ) + if (downloadState is DownloadState.Connecting) dots else "",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Composable
private fun Logo(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    isIndeterminate: Boolean = false
) {
    val shape = remember { ScallopShape() }
    val rotationState = rememberRotationState()
    Box(
        modifier
            .detectRotation(rotationState), contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .displayRotation(rotationState)
                .clip(shape)
                .background(Color(0xFF2F3032))
                .aspectRatio(1f)
                .fillMaxSize()
        )
        val animatedProgress by animateFloatAsState(progress(), label = "")
        val animatedWidth = animateFloatAsState(
            if (rotationState.isRotationInProgress) 8f
            else if (animatedProgress != 100f) 3f
            else 1f,
            label = ""
        ).value * MaterialTheme.spacing.extraSmall
        val start =
            if (animatedProgress != 100f) rememberInfiniteTransition(label = "").animateFloat(
                initialValue = -90f,
                targetValue = 270f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 5000,
                        easing = LinearEasing
                    ), repeatMode = RepeatMode.Restart
                ),
                label = ""
            ).value else -90f
        val arc = if (isIndeterminate) indeterminateArc() else remember(start, animatedProgress) {
            arc(
                degrees(start),
                degrees(animatedProgress * 3.6f)
            )
        }
        ArcBorder(
            arc,
            BorderStroke(animatedWidth, MaterialTheme.colorScheme.primary),
            Modifier
                .displayRotation(rotationState, layer = 1f)
                .aspectRatio(1f)
                .fillMaxSize()
                .clip(shape), shape
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "",
                modifier = Modifier
                    .scale(1.3f)
                    .fillMaxSize()
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop
            )
        }
    }
}