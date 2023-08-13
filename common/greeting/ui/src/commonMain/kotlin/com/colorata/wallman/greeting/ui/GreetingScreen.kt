package com.colorata.wallman.greeting.ui

import android.os.Build
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.colorata.wallman.core.data.Destinations
import com.colorata.wallman.core.data.DownloadState
import com.colorata.wallman.core.data.MaterialNavGraphBuilder
import com.colorata.wallman.core.data.Strings
import com.colorata.wallman.core.data.animation
import com.colorata.wallman.core.data.flatComposable
import com.colorata.wallman.core.data.rememberString
import com.colorata.wallman.core.data.viewModel
import com.colorata.wallman.core.ui.animation.animateVisibility
import com.colorata.wallman.core.ui.components.FullScreenLoadingIndicator
import com.colorata.wallman.core.ui.modifiers.drawWithMask
import com.colorata.wallman.core.ui.modifiers.runWhen
import com.colorata.wallman.core.ui.theme.emphasizedVerticalSlide
import com.colorata.wallman.core.ui.theme.spacing
import com.colorata.wallman.greeting.api.GreetingDestination
import com.colorata.wallman.greeting.viewmodel.GreetingScreenEvent
import com.colorata.wallman.greeting.viewmodel.GreetingScreenState
import com.colorata.wallman.greeting.viewmodel.GreetingViewModel
import com.colorata.wallman.wallpapers.WallpapersModule

context(WallpapersModule)
fun MaterialNavGraphBuilder.greetingScreen() {
    flatComposable(Destinations.GreetingDestination()) {
        GreetingScreen()
    }
}

context(WallpapersModule)
@Composable
fun GreetingScreen(modifier: Modifier = Modifier) {
    val viewModel = viewModel { GreetingViewModel() }
    val state by viewModel.state.collectAsStateWithLifecycle()
    GreetingScreen(state, modifier)
}

@Composable
fun GreetingScreen(state: GreetingScreenState, modifier: Modifier = Modifier) {
    val isDownloaded by remember(state) {
        derivedStateOf { state.downloadState is DownloadState.Downloaded }
    }
    val isError by remember(state) {
        derivedStateOf { state.downloadState is DownloadState.Error }
    }

    FullScreenLoadingIndicator(state.downloadState, modifier) { visible ->
        ContinueButton(
            visible && isDownloaded,
            onClick = { state.onEvent(GreetingScreenEvent.Continue) },
            modifier = Modifier.runWhen(visible && isDownloaded) { zIndex(3f) }
        )
        ErrorButton(visible && isError, onClick = {
            state.onEvent(GreetingScreenEvent.Retry)
        })
    }
}

@Composable
private fun ContinueButton(visible: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier, contentAlignment = Alignment.Center
    ) {
        val surfaceColor = MaterialTheme.colorScheme.surface
        val radiusMultiplier by animateFloatAsState(
            if (visible) 0.5f else 0.01f,
            animationSpec = MaterialTheme.animation.emphasized(
                durationMillis = MaterialTheme.animation.durationSpec.extraLong4,
                delayMillis = MaterialTheme.animation.durationSpec.long1
            ),
            label = ""
        )
        val sweepGradientColors = listOf(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.primaryContainer
        )
        val rotation = rememberInfiniteTransition(label = "").animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                tween(
                    MaterialTheme.animation.durationSpec.extraLong4 * 10,
                    easing = LinearEasing
                )
            ),
            label = ""
        )
        Box(
            Modifier
                .animateVisibility(
                    visible,
                    MaterialTheme.animation.emphasizedVerticalSlide()
                )
                .graphicsLayer {
                    // not rotating on older versions because gradient create almost
                    // invisible outlined rectangle that rotates too
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        rotationZ = rotation.value
                    }
                }
                .blur(30.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                .drawWithMask {
                    drawRect(
                        Brush.radialGradient(
                            listOf(
                                Color.Transparent, surfaceColor
                            ), center, radius = size.minDimension * radiusMultiplier
                        ),
                        blendMode = BlendMode.DstOut
                    )
                }
                .drawBehind {
                    drawRect(
                        Brush.sweepGradient(
                            sweepGradientColors, center
                        )
                    )
                }
                .fillMaxSize())
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            BigButton(
                visible = visible,
                onClick = { if (visible) onClick() }
            )
        }
    }
}

@Composable
private fun ErrorButton(
    visible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BigButton(
        visible = visible,
        onClick = onClick,
        label = rememberString(Strings.retry),
        icon = Icons.Default.Refresh,
        modifier = modifier
    )
}

@Composable
private fun BigButton(
    visible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = rememberString(Strings.startExploring),
    icon: ImageVector = Icons.Default.KeyboardArrowRight
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    ElevatedButton(
        onClick = { if (visible) onClick() },
        modifier
            .runWhen(visible) { zIndex(3f) }
            .animateVisibility(
                visible,
                MaterialTheme.animation.emphasizedVerticalSlide()
            )
            .height(MaterialTheme.spacing.extraLarge * 3),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = surfaceColor.copy(
                alpha = 0.3f
            )
        )
    ) {
        Spacer(Modifier.width(MaterialTheme.spacing.extraLarge * 2))
        Text(
            text = label,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Spacer(Modifier.width(MaterialTheme.spacing.small))
        Icon(
            icon,
            null,
            Modifier
                .size(18.dp)
                .align(Alignment.CenterVertically)
        )
        Spacer(Modifier.width(MaterialTheme.spacing.extraLarge * 2))
    }
}