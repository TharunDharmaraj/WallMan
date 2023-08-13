package com.colorata.wallman.settings.overview.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.colorata.wallman.core.ui.components.FontVariables
import com.colorata.wallman.core.ui.components.Text
import com.colorata.wallman.core.ui.theme.WallManPreviewTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun FancyTextScreen(text: String, modifier: Modifier = Modifier) {
    val fadeList = remember {
        mutableStateListOf<Animatable<Float, AnimationVector1D>>().apply {
            repeat(7) {
                add(Animatable(0f))
            }
        }
    }
    val weightList = remember {
        mutableStateListOf<Animatable<Float, AnimationVector1D>>().apply {
            repeat(7) {
                add(Animatable(200f))
            }
        }
    }
    val scaleList = remember {
        mutableStateListOf<Animatable<Float, AnimationVector1D>>().apply {
            repeat(7) {
                add(Animatable(0.9f))
            }
        }
    }
    val scope = rememberCoroutineScope()
    var job by remember { mutableStateOf<Job?>(null) }
    LaunchedEffect(key1 = true) {
        job = scope.launch {
            val duration = 2500
            repeat(7) {
                scope.launch {
                    delay(abs(3 - it) * 300L)
                    while (true) {
                        scope.launch {
                            weightList[it].animateTo(
                                900f,
                                animationSpec = tween(duration)
                            )
                        }
                        scope.launch { fadeList[it].animateTo(1f, animationSpec = tween(duration)) }
                        scaleList[it].animateTo(1f, animationSpec = tween(duration))
                        delay(50)

                        scope.launch {
                            weightList[it].animateTo(
                                200f,
                                animationSpec = tween(duration)
                            )
                        }
                        scope.launch { fadeList[it].animateTo(0f, animationSpec = tween(duration)) }
                        scaleList[it].animateTo(0.9f, animationSpec = tween(duration))
                        delay(50)

                    }
                }
            }
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                job?.cancel()
            } else if (event == Lifecycle.Event.ON_STOP) {
                job?.cancel()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    var readyToDraw by remember { mutableStateOf(false) }
    var fontSize by remember { mutableIntStateOf(100) }
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        repeat(7) {
            Text(
                text = text,
                modifier = Modifier
                    .drawWithContent {
                        if (readyToDraw) drawContent()
                    }
                    .fillMaxWidth()
                    .weight(1f)
                    .scale(scaleList[it].value)
                    .alpha(fadeList[it].value),
                fontVariables = FontVariables(weight = weightList[it].value, width = 151f),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = fontSize.sp,
                letterSpacing = 20.sp,
                textAlign = TextAlign.Center,
                softWrap = false,
                maxLines = 1,
                onTextLayout = { result ->
                    scope.launch {
                        if (!readyToDraw) {
                            if (result.didOverflowWidth) {
                                fontSize -= 1
                            } else {
                                readyToDraw = true
                            }
                        }
                    }
                }
            )
        }
    }
}

@Preview
@Composable
private fun FancyTextScreenSample() {
    WallManPreviewTheme {
        FancyTextScreen(text = "07:30")
    }
}