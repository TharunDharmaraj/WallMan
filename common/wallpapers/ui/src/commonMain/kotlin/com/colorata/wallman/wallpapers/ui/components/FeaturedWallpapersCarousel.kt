package com.colorata.wallman.wallpapers.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.colorata.wallman.core.data.animation
import com.colorata.wallman.core.data.launchIO
import com.colorata.wallman.core.data.rememberString
import com.colorata.wallman.core.data.storagedImageBitmap
import com.colorata.wallman.core.ui.animation.animateVisibility
import com.colorata.wallman.core.ui.list.animateVisibility
import com.colorata.wallman.core.ui.list.rememberVisibilityList
import com.colorata.wallman.core.ui.modifiers.Padding
import com.colorata.wallman.core.ui.modifiers.navigationStartPadding
import com.colorata.wallman.core.ui.theme.WallManContentTheme
import com.colorata.wallman.core.ui.theme.WallManPreviewTheme
import com.colorata.wallman.core.ui.theme.emphasizedVerticalSlide
import com.colorata.wallman.core.ui.theme.spacing
import com.colorata.wallman.wallpapers.WallpaperI
import com.colorata.wallman.wallpapers.firstBaseWallpaper
import com.colorata.wallman.wallpapers.firstPreviewRes
import com.colorata.wallman.wallpapers.walls
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue


@OptIn(ExperimentalFoundationApi::class)
private fun PagerState.calculateOffsetForIndex(index: Int) =
    (currentPage - index + currentPageOffsetFraction)
        .absoluteValue.coerceIn(0f, 1f)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeaturedWallpapersCarousel(
    wallpapers: ImmutableList<WallpaperI>,
    onClick: (WallpaperI) -> Unit,
    modifier: Modifier = Modifier,
    onWallpaperRotation: (WallpaperI) -> Unit = {}
) {
    val state = rememberPagerState(Int.MAX_VALUE / 2) {
        Int.MAX_VALUE
    }
    val density = LocalDensity.current
    val anim = MaterialTheme.animation
    val scope = rememberCoroutineScope()
    val currentProgress = remember { Animatable(0f) }
    val visibleWallpapers = rememberVisibilityList(keys = arrayOf(wallpapers)) { wallpapers }
    var indicatorsVisible by remember { mutableStateOf(false) }

    var containerWidth by remember { mutableStateOf(0.dp) }

    val sizeOfWallpapers = wallpapers.size.coerceAtLeast(1)

    LaunchedEffect(wallpapers) {
        launch {
            val page = state.currentPage
            visibleWallpapers.visible[page % sizeOfWallpapers] = true
            delay(100)
            visibleWallpapers.visible[(page - 1) % sizeOfWallpapers] = true
            visibleWallpapers.visible[(page + 1) % sizeOfWallpapers] = true
            visibleWallpapers.animateVisibility(
                visible = true,
                startIndex = page % sizeOfWallpapers
            )
        }
        delay(300)
        indicatorsVisible = true
    }
    LaunchedEffect(Unit) {
        while (true) {
            launchIO({}) {
                val page = state.currentPage
                currentProgress.animateTo(
                    1f, animationSpec = tween(5000, easing = LinearEasing)
                )
                if (state.currentPage == page && currentProgress.value == 1f) state.animateScrollToPage(
                    state.currentPage + 1,
                    animationSpec = anim.emphasized(anim.durationSpec.extraLong4)
                )
                currentProgress.animateTo(0f)
            }.join()
            delay(1000)
        }
    }
    LaunchedEffect(Unit) {
        snapshotFlow { state.isScrollInProgress }.collect {
            if (state.isScrollInProgress) {
                currentProgress.stop()
                currentProgress.animateTo(0f)
            }
        }
    }
    LaunchedEffect(Unit) {
        snapshotFlow { state.currentPage }.collect { index ->
            onWallpaperRotation(wallpapers[index % sizeOfWallpapers])
        }
    }
    Column(
        modifier
            .fillMaxWidth()
            .onSizeChanged {
                containerWidth = density.run { it.width.toDp() }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
    ) {
        HorizontalPager(
            state,
            contentPadding = PaddingValues(horizontal =
            remember(containerWidth) {
                (containerWidth - 192.dp).coerceAtLeast(0.dp) / 2
            } + Padding.navigationStartPadding() / 2),
            modifier = Modifier
                .fillMaxWidth()
                .height(384.dp),
            pageSize = PageSize.Fixed(192.dp),
            key = {
                it
            }
        ) { index ->
            var isSelected by remember { mutableStateOf(false) }
            val scale by animateFloatAsState(
                if (isSelected) 1.1f else 1f,
                label = "",
                animationSpec = tween(200, easing = EaseInSine)
            )
            val currentWallpaper = wallpapers[index % sizeOfWallpapers]

            val previewImage = storagedImageBitmap(currentWallpaper.firstPreviewRes())
            val clickHandler = remember {
                {
                    scope.launch {
                        if (state.currentPage != index) state.animateScrollToPage(
                            index,
                            animationSpec = anim.emphasized(anim.durationSpec.extraLong4)
                        ) else {
                            isSelected = true
                            delay(50)
                            onClick(currentWallpaper)
                        }
                    }
                }
            }
            Box(
                Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }, contentAlignment = Alignment.Center
            ) {
                val cornerRadius = MaterialTheme.spacing.extraLarge
                Box(
                    Modifier
                        .animateVisibility(
                            visibleWallpapers.visible[index % sizeOfWallpapers],
                            MaterialTheme.animation.emphasizedVerticalSlide()
                        )
                        .clip(RoundedCornerShape(cornerRadius))
                        .clickable {
                            clickHandler()
                        }
                        .testTag("Wallpaper")
                        .graphicsLayer {
                            val pageOffset = state.calculateOffsetForIndex(index)
                            alpha = lerp(0.5f, 1f, 1f - pageOffset)
                            scaleX = lerp(0.85f, 1f, 1f - pageOffset)
                            scaleY = lerp(0.85f, 1f, 1f - pageOffset)
                        }
                        .drawWithContent {
                            val pageOffset = state.calculateOffsetForIndex(index)
                            clipPath(Path().apply {
                                this.addRoundRect(
                                    RoundRect(
                                        Rect(
                                            left = 48.dp.toPx() * pageOffset,
                                            right = size.width - 48.dp.toPx() * pageOffset,
                                            top = 0f,
                                            bottom = size.height
                                        ), CornerRadius(cornerRadius.toPx())
                                    )
                                )
                            }, clipOp = ClipOp.Intersect) {
                                this@drawWithContent.drawContent()
                            }
                        }) {
                    Image(
                        bitmap = previewImage,
                        contentDescription = "",
                        Modifier
                            .align(Alignment.Center)
                            .fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    WallManContentTheme(currentWallpaper.firstPreviewRes()) {
                        Button(
                            onClick = {
                                clickHandler()
                            },
                            Modifier
                                .graphicsLayer {
                                    val pageOffset = state.calculateOffsetForIndex(index)
                                    alpha = maxOf(1f - pageOffset * 2, 0f)
                                }
                                .align(
                                    Alignment.BottomCenter
                                )
                                .padding(MaterialTheme.spacing.large)
                                .drawWithContent {
                                    clipPath(Path().apply {
                                        val pageOffset = state.calculateOffsetForIndex(index)
                                        this.addRoundRect(
                                            RoundRect(
                                                Rect(
                                                    left = size.width / 2 * pageOffset,
                                                    right = size.width - size.width / 2 * pageOffset,
                                                    top = 0f,
                                                    bottom = size.height
                                                ), CornerRadius(size.height / 2)
                                            )
                                        )
                                    }, clipOp = ClipOp.Intersect) {
                                        this@drawWithContent.drawContent()
                                    }
                                }
                                .heightIn(min = 48.dp)
                        ) {
                            Text(
                                rememberString(string = currentWallpaper.firstBaseWallpaper().shortName),
                                maxLines = 2,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
        Row(
            Modifier.animateVisibility(
                indicatorsVisible,
                MaterialTheme.animation.emphasizedVerticalSlide()
            ),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
        ) {
            wallpapers.forEachIndexed { index, _ ->
                val pageOffset by remember(wallpapers) {
                    derivedStateOf {
                        (state.currentPage % sizeOfWallpapers - index + state.currentPageOffsetFraction).absoluteValue.coerceIn(
                            0f,
                            1f
                        )
                    }
                }
                val isSelected by remember(wallpapers) { derivedStateOf { state.currentPage % sizeOfWallpapers == index } }
                val animatedWidth by animateDpAsState(
                    12.dp + 24.dp * (1f - pageOffset),
                    label = ""
                )
                val animatedBackground by animateColorAsState(
                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    label = ""
                )
                val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
                Box(
                    Modifier
                        .clip(CircleShape)
                        .drawBehind {
                            drawRect(surfaceVariant)
                            if (isSelected) drawRoundRect(
                                animatedBackground,
                                size = size.copy(width = size.width * currentProgress.value),
                                cornerRadius = CornerRadius(size.height / 2)
                            )
                        }
                        .width(animatedWidth)
                        .height(12.dp))
            }
        }
    }
}

@Preview
@Composable
private fun FeaturedWallpapersPreview() {
    WallManPreviewTheme {
        FeaturedWallpapersCarousel(walls.subList(0, 10), onClick = {})
    }
}