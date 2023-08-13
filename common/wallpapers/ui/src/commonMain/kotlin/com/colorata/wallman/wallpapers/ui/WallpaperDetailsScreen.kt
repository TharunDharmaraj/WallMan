package com.colorata.wallman.wallpapers.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.colorata.animateaslifestyle.material3.shapes.ScallopShape
import com.colorata.animateaslifestyle.shapes.ExperimentalShapeApi
import com.colorata.wallman.core.data.Destinations
import com.colorata.wallman.core.data.MaterialNavGraphBuilder
import com.colorata.wallman.core.data.Polyglot
import com.colorata.wallman.core.data.Strings
import com.colorata.wallman.core.data.animation
import com.colorata.wallman.core.data.flatComposable
import com.colorata.wallman.core.data.formatted
import com.colorata.wallman.core.data.icons.InstallMobile
import com.colorata.wallman.core.data.materialSharedAxisX
import com.colorata.wallman.core.data.materialSharedAxisY
import com.colorata.wallman.core.data.parameter
import com.colorata.wallman.core.data.rememberString
import com.colorata.wallman.core.data.simplifiedLocaleOf
import com.colorata.wallman.core.data.storagedImageBitmap
import com.colorata.wallman.core.data.viewModel
import com.colorata.wallman.core.ui.LightDarkPreview
import com.colorata.wallman.core.ui.animation.animateVisibility
import com.colorata.wallman.core.ui.components.GradientType
import com.colorata.wallman.core.ui.components.ScreenBackground
import com.colorata.wallman.core.ui.components.rememberModalBottomSheetState
import com.colorata.wallman.core.ui.list.VisibilityList
import com.colorata.wallman.core.ui.list.animatedAtLaunch
import com.colorata.wallman.core.ui.list.rememberVisibilityList
import com.colorata.wallman.core.ui.modifiers.detectRotation
import com.colorata.wallman.core.ui.modifiers.displayRotation
import com.colorata.wallman.core.ui.modifiers.drawWithMask
import com.colorata.wallman.core.ui.modifiers.rememberRotationState
import com.colorata.wallman.core.ui.theme.WallManContentTheme
import com.colorata.wallman.core.ui.theme.WallManPreviewTheme
import com.colorata.wallman.core.ui.theme.emphasizedVerticalSlide
import com.colorata.wallman.core.ui.theme.spacing
import com.colorata.wallman.core.ui.util.LocalWindowSizeConfiguration
import com.colorata.wallman.ui.icons.SdCard
import com.colorata.wallman.wallpapers.BaseWallpaper
import com.colorata.wallman.wallpapers.Chip
import com.colorata.wallman.wallpapers.DynamicWallpaper
import com.colorata.wallman.wallpapers.StaticWallpaperApplyVariant
import com.colorata.wallman.wallpapers.WallpaperDetailsDestination
import com.colorata.wallman.wallpapers.WallpaperI
import com.colorata.wallman.wallpapers.WallpapersModule
import com.colorata.wallman.wallpapers.sizeInMb
import com.colorata.wallman.wallpapers.supportsDynamicWallpapers
import com.colorata.wallman.wallpapers.ui.components.BigChip
import com.colorata.wallman.wallpapers.ui.components.StaticWallpaperChooser
import com.colorata.wallman.wallpapers.ui.components.WallpaperVariants
import com.colorata.wallman.wallpapers.viewmodel.WallpaperDetailsScreenEvent
import com.colorata.wallman.wallpapers.viewmodel.WallpaperDetailsScreenState
import com.colorata.wallman.wallpapers.viewmodel.WallpaperDetailsViewModel
import com.colorata.wallman.wallpapers.walls
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

context(WallpapersModule)
fun MaterialNavGraphBuilder.wallpaperDetailsScreen() {
    flatComposable(Destinations.WallpaperDetailsDestination()) {
        val index by parameter()
        WallpaperDetailsScreen(wallpaperIndex = index?.toInt() ?: 0)
    }
}

context(WallpapersModule)
@Composable
fun WallpaperDetailsScreen(wallpaperIndex: Int, modifier: Modifier = Modifier) {
    val viewModel = viewModel { WallpaperDetailsViewModel(wallpaperIndex) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    WallpaperDetailsScreen(state, modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WallpaperDetailsScreen(
    state: WallpaperDetailsScreenState,
    modifier: Modifier = Modifier
) {
    val selectedBaseWallpaper = state.selectedWallpaper

    val scrollState = rememberScrollState()
    val previewImage = storagedImageBitmap(state.selectedWallpaper.previewRes)
    val animationSpec = MaterialTheme.animation.emphasizedVerticalSlide()
    val animList = rememberVisibilityList {
        List(8) {}
    }.animatedAtLaunch()

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        animationSpec = MaterialTheme.animation.emphasized()
    )
    val windowSize = LocalWindowSizeConfiguration.current
    WallManContentTheme(state.selectedWallpaper.previewRes) {
        if (state.showPermissionRequest) {
            PermissionRequestDialog(onDismiss = {
                state.onEvent(WallpaperDetailsScreenEvent.DismissPermissionRequest)
            }, onConfirm = {
                state.onEvent(WallpaperDetailsScreenEvent.GoToInstallAppsPermissionsPage)
            })
        }
        if (state.showPerformanceWarning) {
            PerformanceWarningDialog(onDismiss = {
                state.onEvent(WallpaperDetailsScreenEvent.DismissPerformanceWarning)
            }, onConfirm = {
                state.onEvent(WallpaperDetailsScreenEvent.ProceedPerformanceWarning)
            })
        }

        val offset = remember { mutableStateOf(Float.MAX_VALUE) }
        LaunchedEffect(Unit) {
            snapshotFlow { runCatching { bottomSheetState.requireOffset() }.getOrNull() }
                .collectLatest { newOffset ->
                    newOffset?.let { offset.value = it }
                }
        }

        Box(
            modifier
                .graphicsLayer {
                    val fraction = offset.value / size.height
                    val scale =
                        1f - (1f - (fraction).coerceIn(
                            0f,
                            1f
                        )) / 10f
                    if (fraction in 0f..1f) {
                        scaleX = scale
                        scaleY = scale
                        translationY =
                            -(1f - fraction) * 300
                    }
                }
                .clip(MaterialTheme.shapes.large)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            if (windowSize.widthSizeClass == WindowWidthSizeClass.Compact) {
                ScreenBackground(previewImage)
                Column(
                    Modifier
                        .verticalScroll(scrollState)
                        .padding(MaterialTheme.spacing.extraLarge)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
                ) {
                    PreviewImage(
                        resource = selectedBaseWallpaper.previewRes,
                        downloadProgress = { state.downloadProgress },
                        Modifier
                            .zIndex(3f)
                            .animateVisibility(animList.visible[0], animationSpec)
                    )
                    DescriptionAndActions(state, visibilityList = animList)
                }
                BottomBar(
                    state, Modifier
                        .align(Alignment.BottomCenter)
                        .animateVisibility(animList.visible[6], animationSpec)
                )
            } else {
                Row(Modifier.fillMaxSize()) {
                    Box(Modifier.weight(1f)) {
                        ScreenBackground(
                            previewImage,
                            gradientType = GradientType.Horizontal,
                            imageFraction = 1f
                        )
                        PreviewImage(
                            resource = selectedBaseWallpaper.previewRes,
                            downloadProgress = { state.downloadProgress },
                            Modifier
                                .padding(MaterialTheme.spacing.extraLarge)
                                .fillMaxSize()
                                .zIndex(3f)
                                .animateVisibility(animList.visible[0], animationSpec)
                        )
                    }
                    Box(
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(
                                Modifier
                                    .verticalScroll(scrollState)
                                    .padding(MaterialTheme.spacing.extraLarge),
                                verticalArrangement = Arrangement.Center
                            ) {
                                DescriptionAndActions(state, visibilityList = animList)
                            }
                        }
                        BottomBar(
                            state,
                            Modifier
                                .align(Alignment.BottomCenter)
                                .animateVisibility(animList.visible[6], animationSpec),
                            fullWidth = false
                        )
                    }
                }
            }
        }
        StaticWallpaperChooserSheet(
            onDismiss = {
                state.onEvent(WallpaperDetailsScreenEvent.DismissStaticWallpaperChooser)
            },
            onClick = { state.onEvent(WallpaperDetailsScreenEvent.ChooseStaticWallpaperType(it)) },
            sheetState = bottomSheetState,
            visible = state.showStaticWallpaperChooser
        )
    }
}

@LightDarkPreview
@Composable
private fun WallpaperDetailsPreview() {
    WallManPreviewTheme {
        WallpaperDetailsScreen(
            WallpaperDetailsScreenState(
                walls[42],
                actionType = WallpaperI.ActionType.Install(true),
                cacheState = DynamicWallpaper.DynamicWallpaperCacheState.Cached,
                downloadProgress = 50f,
                selectedWallpaper = walls[42].dynamicWallpapers[0],
                selectedWallpaperType = WallpaperI.SelectedWallpaperType.Dynamic,
                wallpaperVariants = walls[42].staticWallpapers,
                onEvent = { })
        )
    }
}

@Composable
private fun Variants(
    wallpaperVariants: ImmutableList<BaseWallpaper>,
    selectedWallpaper: BaseWallpaper,
    onClick: (updatedWallpaper: BaseWallpaper) -> Unit,
    modifier: Modifier = Modifier
) {
    val variants = remember(wallpaperVariants) {
        wallpaperVariants.map { it.previewRes }.toImmutableList()
    }
    AnimatedContent(
        targetState = variants,
        transitionSpec = { materialSharedAxisX(true, 100) using SizeTransform(clip = false) },
        label = "",
        modifier = modifier
    ) { updatedVariants ->
        WallpaperVariants(
            wallpapers = updatedVariants,
            selectedWallpaper = selectedWallpaper.previewRes,
            onClick = { updatedRes ->
                onClick(wallpaperVariants.first { it.previewRes == updatedRes })
            })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StaticWallpaperChooserSheet(
    onDismiss: () -> Unit,
    onClick: (StaticWallpaperApplyVariant) -> Unit,
    sheetState: SheetState,
    modifier: Modifier = Modifier,
    visible: Boolean = false
) {
    val scope = rememberCoroutineScope()
    if (visible) ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        modifier,
        sheetState = sheetState,
        windowInsets = WindowInsets(0.dp)
    ) {
        StaticWallpaperChooser(
            onClick = {
                scope.launch {
                    sheetState.hide()
                    if (!sheetState.isVisible) onClick(it)
                }
            },
            Modifier
                .padding(bottom = MaterialTheme.spacing.extraLarge)
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(horizontal = MaterialTheme.spacing.extraLarge)
        )
    }
}

@OptIn(ExperimentalShapeApi::class)
@Composable
private fun PreviewImage(
    resource: String,
    downloadProgress: () -> Float,
    modifier: Modifier = Modifier
) {
    val shape = remember { ScallopShape(density = 100f) }
    val rotationState = rememberRotationState()
    val animation = MaterialTheme.animation

    Box(
        modifier
            .detectRotation(rotationState)
            .statusBarsPadding(), contentAlignment = Alignment.Center
    ) {
        Arc(progress = { downloadProgress() }, width = {
            if (rotationState.isRotationInProgress) 8f
            else 0f
        }, Modifier
            .displayRotation(rotationState)
            .fillMaxSize(0.9f)
            .aspectRatio(1f), shape
        )
        AnimatedContent(
            targetState = resource, transitionSpec = {
                fadeIn(animation.emphasized()) togetherWith fadeOut(animation.emphasized())
            }, label = "", modifier = Modifier
                .fillMaxSize(0.9f)
                .aspectRatio(1f)
        ) { imageName ->
            Image(
                bitmap = storagedImageBitmap(imageName), contentDescription = "", modifier = Modifier
                    .displayRotation(rotationState, layer = 0.5f)
                    .clip(shape)
                    .fillMaxSize()
                    .aspectRatio(1f), contentScale = ContentScale.Crop
            )
        }
        Arc(progress = { downloadProgress() }, width = { progress ->
            if (rotationState.isRotationInProgress) 0f
            else if (progress == 100f) 1f
            else 3f
        }, Modifier
            .displayRotation(rotationState, layer = 1f)
            .fillMaxSize(0.9f)
            .aspectRatio(1f), shape
        )
    }
}

@Composable
private fun Arc(
    progress: () -> Float,
    width: (progress: Float) -> Float,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape
) {
    val animatedProgress by animateFloatAsState(progress(), label = "")
    val start = rememberInfiniteTransition(label = "").animateFloat(
        initialValue = -90f,
        targetValue = 270f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 5000,
                easing = LinearEasing
            ), repeatMode = RepeatMode.Restart
        ),
        label = ""
    )
    val extraSmallPadding = MaterialTheme.spacing.extraSmall
    val animatedWidth = animateDpAsState(targetValue = remember {
        derivedStateOf {
            extraSmallPadding * width(animatedProgress)
        }
    }.value, label = "")
    val borderColor = MaterialTheme.colorScheme.primary
    val layoutDirection = LocalLayoutDirection.current
    val density = LocalDensity.current
    Box(modifier = modifier.clip(shape)) {
        Canvas(
            modifier = Modifier
                .drawWithMask {
                    if (animatedWidth.value != 0.dp) {
                        drawArc(
                            BorderStroke(animatedWidth.value, borderColor).brush,
                            start.value + 3.6f * animatedProgress,
                            360f - 3.6f * animatedProgress,
                            useCenter = true,
                            blendMode = BlendMode.DstOut
                        )
                    }
                }
                .fillMaxSize()
        ) {
            if (animatedWidth.value != 0.dp) {
                val borderStroke = BorderStroke(animatedWidth.value, borderColor)
                drawOutline(
                    shape.createOutline(size, layoutDirection, density),
                    brush = borderStroke.brush,
                    style = Stroke(borderStroke.width.toPx(), cap = StrokeCap.Round)
                )
            }
        }
    }
}

@Composable
fun DescriptionAndActions(
    state: WallpaperDetailsScreenState,
    visibilityList: VisibilityList<Unit>,
    modifier: Modifier = Modifier
) {
    val animationSpec = MaterialTheme.animation.emphasizedVerticalSlide()
    val wallpaper = state.wallpaper
    val selectedBaseWallpaper = state.selectedWallpaper
    Column(
        modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
    ) {
        PreviewName(
            selectedBaseWallpaper.previewName, Modifier
                .align(Alignment.Start)
                .animateVisibility(visibilityList.visible[1], animationSpec)
        )
        Description(
            selectedBaseWallpaper.description, Modifier
                .align(Alignment.Start)
                .animateVisibility(visibilityList.visible[2], animationSpec)
        )
        WallpaperTypeSelector(
            supportsDynamicWallpaper = remember(wallpaper) { wallpaper.supportsDynamicWallpapers() },
            selectedWallpaperType = state.selectedWallpaperType,
            onClick = {
                state.onEvent(
                    WallpaperDetailsScreenEvent.SelectWallpaperType(
                        it
                    )
                )
            },
            Modifier.animateVisibility(visibilityList.visible[3], animationSpec)
        )
        val chips = remember {
            persistentListOf<Chip>().mutate {
                if (selectedBaseWallpaper.coordinates != null) it.add(
                    Chip(
                        Strings.goToMaps,
                        Icons.Default.LocationOn
                    )
                )
                if (wallpaper.supportsDynamicWallpapers()) it.add(
                    Chip(
                        Strings.size.formatted(
                            wallpaper.parent.sizeInMb()
                        ), Icons.Default.SdCard
                    )
                )
                it.add(Chip(simplifiedLocaleOf(wallpaper.author), Icons.Filled.AccountCircle))
            }
        }
        Chips(chips = chips, onClick = {
            val index = chips.indexOf(it)
            if (index == 0) state.onEvent(WallpaperDetailsScreenEvent.GoToMaps)
        }, Modifier.animateVisibility(visibilityList.visible[4], animationSpec))
        Variants(state.wallpaperVariants, selectedWallpaper = state.selectedWallpaper, onClick = {
            state.onEvent(
                WallpaperDetailsScreenEvent.SelectBaseWallpaper(
                    it
                )
            )
        }, Modifier.animateVisibility(visibilityList.visible[5], animationSpec))
        Spacer(Modifier.height(80.dp))
    }
}

@Composable
private fun PreviewName(previewName: Polyglot, modifier: Modifier = Modifier) {
    AnimatedContent(targetState = previewName, transitionSpec = {
        materialSharedAxisX(true, 100) using SizeTransform(clip = false)
    }, modifier = modifier, label = "") { name ->
        Text(
            text = rememberString(string = name),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun Description(description: Polyglot, modifier: Modifier = Modifier) {
    AnimatedContent(targetState = description, transitionSpec = {
        materialSharedAxisX(true, 100) using SizeTransform(clip = false)
    }, modifier = modifier, label = "") { animatedDescription ->
        Text(
            text = rememberString(string = animatedDescription),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Chips(
    chips: ImmutableList<Chip>,
    onClick: (Chip) -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ) {
        chips.forEach { chip ->
            AssistChip(onClick = { onClick(chip) }, label = {
                Text(text = rememberString(chip.previewName))
            }, leadingIcon = {
                Icon(imageVector = chip.icon, contentDescription = "")
            })
        }
    }
}

@Composable
private fun WallpaperTypeSelector(
    supportsDynamicWallpaper: Boolean,
    selectedWallpaperType: WallpaperI.SelectedWallpaperType,
    onClick: (WallpaperI.SelectedWallpaperType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier) {
        if (supportsDynamicWallpaper) {
            BigChip(
                onClick = {
                    onClick(WallpaperI.SelectedWallpaperType.Dynamic)
                },
                selected = selectedWallpaperType == WallpaperI.SelectedWallpaperType.Dynamic,
                text = WallpaperI.SelectedWallpaperType.Dynamic.label,
                Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
        BigChip(
            onClick = {
                onClick(WallpaperI.SelectedWallpaperType.Static)
            },
            selected = selectedWallpaperType == WallpaperI.SelectedWallpaperType.Static,
            text = WallpaperI.SelectedWallpaperType.Static.label,
            Modifier.weight(1f)
        )
    }
}

@Composable
fun PermissionRequestDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(onDismissRequest = {
        onDismiss()
    }, confirmButton = {
        Button(onClick = { onConfirm() }) {
            Text(rememberString(Strings.ok))
        }
    }, icon = {
        Icon(Icons.Default.InstallMobile, contentDescription = null)
    }, title = {
        Text(rememberString(Strings.permissionNeeded))
    }, text = {
        Text(rememberString(Strings.permissionNeededDescription))
    }, modifier = modifier)
}

@Composable
fun PerformanceWarningDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(onDismissRequest = {
        onDismiss()
    }, confirmButton = {
        Button(onClick = { onConfirm() }) {
            Text(rememberString(Strings.download))
        }
    }, dismissButton = {
        OutlinedButton(onClick = { onDismiss() }) {
            Text(rememberString(Strings.cancel))
        }
    }, icon = {
        Icon(Icons.Default.Warning, contentDescription = null)
    }, title = {
        Text(rememberString(Strings.performanceWarning))
    }, text = {
        Text(rememberString(Strings.performanceWarningDescription))
    }, modifier = modifier)
}

@Composable
private fun BottomBar(
    state: WallpaperDetailsScreenState,
    modifier: Modifier = Modifier,
    fullWidth: Boolean = true,
) {
    val horizontalPadding =
        if (fullWidth) MaterialTheme.spacing.extraLarge else MaterialTheme.spacing.medium
    val verticalPadding =
        if (fullWidth) MaterialTheme.spacing.large else MaterialTheme.spacing.small
    Row(
        modifier
            .then(
                if (!fullWidth) Modifier
                    .widthIn(max = 400.dp)
                    .padding(
                        horizontal = MaterialTheme.spacing.extraLarge,
                        vertical = MaterialTheme.spacing.small
                    )
                    .navigationBarsPadding()
                    .clip(CircleShape) else Modifier
            )
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = horizontalPadding, vertical = verticalPadding)
            .then(if (fullWidth) Modifier.navigationBarsPadding() else Modifier),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = {
                state.onEvent(WallpaperDetailsScreenEvent.ClickOnDownload)
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(
                MaterialTheme.spacing.extraLarge,
                MaterialTheme.spacing.extraSmall,
                MaterialTheme.spacing.extraSmall,
                MaterialTheme.spacing.extraLarge
            ),
            enabled = state.selectedWallpaperType == WallpaperI.SelectedWallpaperType.Dynamic &&
                    remember(
                        state.wallpaper
                    ) { state.wallpaper.supportsDynamicWallpapers() }
        ) {
            AnimatedContent(targetState = state.cacheState, transitionSpec = {
                materialSharedAxisY(true, 100)
            }, label = "") {
                Text(text = rememberString(it.label))
            }
        }

        Button(
            enabled = state.actionType.available,
            onClick = {
                state.onEvent(WallpaperDetailsScreenEvent.ClickOnActionButton)
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(
                MaterialTheme.spacing.extraSmall,
                MaterialTheme.spacing.extraLarge,
                MaterialTheme.spacing.extraLarge,
                MaterialTheme.spacing.extraSmall
            )
        ) {
            AnimatedContent(targetState = state.selectedWallpaperType.icon, transitionSpec = {
                materialSharedAxisY(true, 100)
            }, label = "") {
                Icon(imageVector = it, contentDescription = "")
            }
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
            AnimatedContent(targetState = state.actionType.label, transitionSpec = {
                materialSharedAxisY(true, 100)
            }, label = "") {
                Text(text = rememberString(string = it))
            }
        }
    }
}