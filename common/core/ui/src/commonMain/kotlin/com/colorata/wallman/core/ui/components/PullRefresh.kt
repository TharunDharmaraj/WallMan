package com.colorata.wallman.core.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.colorata.animateaslifestyle.material3.indicator.indeterminateArc
import com.colorata.animateaslifestyle.shapes.arc
import com.colorata.animateaslifestyle.shapes.degrees
import com.colorata.wallman.core.data.DownloadState
import com.colorata.wallman.core.data.Strings
import com.colorata.wallman.core.data.animation
import com.colorata.wallman.core.data.displayedText
import com.colorata.wallman.core.data.rememberString
import com.colorata.wallman.core.ui.animation.animateVisibility
import com.colorata.wallman.core.ui.modifiers.Padding
import com.colorata.wallman.core.ui.modifiers.statusBarPadding
import com.colorata.wallman.core.ui.theme.emphasizedVerticalSlide
import com.colorata.wallman.core.ui.theme.spacing
import com.colorata.wallman.core.ui.util.animateObfuscated
import com.colorata.wallman.core.ui.util.animatedDots
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PullRefresh(
    refreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    refreshText: @Composable () -> String = { rememberString(Strings.refreshing) + animatedDots() },
    progress: () -> Float? = { null },
    content: @Composable () -> Unit
) {
    val refreshScope = rememberCoroutineScope()
    val targetHeight = 160.dp
    val threshold = with(LocalDensity.current) { targetHeight.toPx() }

    var currentDistance by remember { mutableFloatStateOf(0f) }

    val pullProgress = currentDistance / threshold

    val animation = MaterialTheme.animation
    fun onPull(pullDelta: Float): Float {
        if (refreshing) return 0f
        val newOffset = (currentDistance + pullDelta).coerceAtLeast(0f)
        val dragConsumed = newOffset - currentDistance
        currentDistance = newOffset
        return dragConsumed
    }

    fun resetTo(
        targetValue: Float = 0f,
        animationSpec: FiniteAnimationSpec<Float> = animation.standardDecelerate()
    ) {
        refreshScope.launch {
            animate(
                initialValue = currentDistance,
                targetValue = targetValue,
                animationSpec = animationSpec
            ) { value, _ ->
                currentDistance = value
            }
        }
    }

    fun onRelease(velocity: Float): Float {
        if (currentDistance > threshold && !refreshing) onRefresh()

        if (pullProgress >= 1f) resetTo(threshold)
        else resetTo(0f)

        // Only consume if the fling is downwards and the indicator is visible
        return if (velocity > 0f && currentDistance > 0f) {
            velocity
        } else {
            0f
        }
    }

    LaunchedEffect(refreshing) {
        if (refreshing) resetTo(threshold)
        else resetTo(0f, animation.emphasized())
    }


    Box(
        modifier
            .pullRefresh(::onPull, ::onRelease)
            .fillMaxSize()
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer {
                    val scale = (1f - pullProgress.coerceAtMost(1f) / 10f)
                    translationY = pullProgress * 200f
                    scaleY = scale
                    scaleX = scale
                }) {
            content()
        }
        PullRefreshIndicator(
            pullProgress = { pullProgress },
            refreshing = refreshing,
            targetHeight = targetHeight, refreshText = refreshText,
            progress = progress
        )
    }
}

@Composable
fun PullRefresh(
    refreshing: Boolean,
    downloadState: DownloadState,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    PullRefresh(
        refreshing = refreshing,
        onRefresh = { onRefresh() },
        refreshText = {
            val dots = animatedDots()
            animateObfuscated(
                rememberString(
                    downloadState.displayedText()
                )
            ) + if (downloadState is DownloadState.Connecting) dots else ""
        },
        progress = {
            when (downloadState) {
                is DownloadState.Downloading -> downloadState.progress
                else -> null
            }
        }) {
        Crossfade(refreshing, label = "") { updatedRefreshing ->
            if (!updatedRefreshing) {
                content()
            } else {
                FullScreenShimmer()
            }
        }
    }
}

@Composable
private fun PullRefreshIndicator(
    pullProgress: () -> Float,
    refreshing: Boolean,
    targetHeight: Dp,
    refreshText: @Composable () -> String,
    progress: () -> Float?,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val isReadyToRefresh by remember(pullProgress()) { derivedStateOf { pullProgress() >= 1f } }
    val iconRotation by animateFloatAsState(if (isReadyToRefresh) 180f else 0f, label = "")
    val targetHeightPx = with(density) { targetHeight.toPx() }
    val statusBarPadding =
        with(density) { (Padding.statusBarPadding() + MaterialTheme.spacing.large).toPx() }
    val isIndeterminate by remember(progress()) { derivedStateOf { progress() == null } }

    val start by rememberInfiniteTransition(label = "").animateFloat(
        initialValue = -90f,
        targetValue = 270f,
        animationSpec = infiniteRepeatable(
            tween(5000, easing = LinearEasing)
        ), label = ""
    )

    val arc = if (isIndeterminate) indeterminateArc() else arc(
        degrees(start),
        degrees((progress() ?: 0f) * 360f)
    )

    val width by animateDpAsState(
        if (refreshing) MaterialTheme.spacing.extraSmall else 0.dp,
        label = ""
    )
    ArcBorder(
        arc = arc,
        border = BorderStroke(
            width,
            if (refreshing) MaterialTheme.colorScheme.primary else Color.Transparent
        ),
        shape = MaterialTheme.shapes.large,
        modifier = modifier
            .graphicsLayer {
                val scale = (1f - pullProgress().coerceAtMost(1f) / 20f)
                translationY =
                    pullProgress() * (targetHeightPx / 2 + statusBarPadding) - targetHeightPx / 2
                scaleY = scale
                scaleX = scale
            }
            .shadow(MaterialTheme.spacing.extraSmall, shape = MaterialTheme.shapes.large)
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .fillMaxWidth()
            .height(targetHeight / 2)) {
        Row(
            Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(
                MaterialTheme.spacing.large,
                Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = null,
                Modifier
                    .graphicsLayer {
                        rotationZ = iconRotation
                    }
                    .animateVisibility(!refreshing))
            Box {
                val readyToRefreshTextSlide = if (isReadyToRefresh) 30f else -30f
                Text(
                    rememberString(Strings.pullToRefresh),
                    Modifier.animateVisibility(
                        !isReadyToRefresh && !refreshing,
                        MaterialTheme.animation.emphasizedVerticalSlide(from = -readyToRefreshTextSlide)
                    )
                )
                Text(
                    rememberString(Strings.releaseToRefresh),
                    Modifier.animateVisibility(
                        isReadyToRefresh && !refreshing,
                        MaterialTheme.animation.emphasizedVerticalSlide(from = readyToRefreshTextSlide)
                    )
                )
                Text(
                    refreshText(),
                    Modifier.animateVisibility(
                        refreshing,
                        MaterialTheme.animation.emphasizedVerticalSlide(from = readyToRefreshTextSlide)
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun PullRefreshPreview() {
    var refreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    PullRefresh(refreshing = refreshing, onRefresh = {
        scope.launch {
            refreshing = true
            delay(4000)
            refreshing = false
        }
    }) {
        LazyColumn {
            items(100) {
                Box(
                    Modifier
                        .padding(10.dp)
                        .background(Color.Red)
                        .fillMaxWidth()
                        .height(100.dp)
                )
            }
        }
    }
}