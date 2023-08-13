package com.colorata.wallman.wallpapers.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.colorata.animateaslifestyle.material3.isCompact
import com.colorata.wallman.core.data.Destinations
import com.colorata.wallman.core.data.MaterialNavGraphBuilder
import com.colorata.wallman.core.data.Strings
import com.colorata.wallman.core.data.flatComposable
import com.colorata.wallman.core.data.rememberString
import com.colorata.wallman.core.data.storagedImageBitmap
import com.colorata.wallman.core.data.util.takeLastOrLess
import com.colorata.wallman.core.data.viewModel
import com.colorata.wallman.core.ui.components.PullRefresh
import com.colorata.wallman.core.ui.theme.spacing
import com.colorata.wallman.core.ui.util.LocalWindowSizeConfiguration
import com.colorata.wallman.wallpapers.MainDestination
import com.colorata.wallman.wallpapers.WallpaperI
import com.colorata.wallman.wallpapers.WallpapersModule
import com.colorata.wallman.wallpapers.firstPreviewRes
import com.colorata.wallman.wallpapers.ui.components.FeaturedWallpapersCarousel
import com.colorata.wallman.wallpapers.ui.components.FilteredWallpaperCards
import com.colorata.wallman.wallpapers.viewmodel.MainScreenEvent
import com.colorata.wallman.wallpapers.viewmodel.MainViewModel
import kotlinx.collections.immutable.toImmutableList

context(WallpapersModule)
fun MaterialNavGraphBuilder.mainScreen() {
    flatComposable(Destinations.MainDestination()) {
        FeaturedScreen()
    }
}

context(WallpapersModule)
@Composable
fun FeaturedScreen(modifier: Modifier = Modifier) {
    val viewModel = viewModel { MainViewModel() }
    val state by viewModel.state.collectAsStateWithLifecycle()
    FeaturedScreen(state, modifier)
}

@Composable
private fun FeaturedScreen(state: MainViewModel.MainScreenState, modifier: Modifier = Modifier) {
    var selectedWallpaper by remember { mutableStateOf<WallpaperI?>(null) }
    var currentImageAsset by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(selectedWallpaper) {
        currentImageAsset = selectedWallpaper?.firstPreviewRes()
    }

    val windowSize = LocalWindowSizeConfiguration.current
    val featuredWallpapers = remember(windowSize, state.featuredWallpapers) {
        state.featuredWallpapers.takeLastOrLess(if (windowSize.isCompact()) 5 else 10)
            .toImmutableList()
    }
    PullRefresh(refreshing = state.refreshing,
        downloadState = state.refreshState,
        onRefresh = { state.onEvent(MainScreenEvent.Refresh) }) {
        if (state.wallpapers.isNotEmpty()) {
            FilteredWallpaperCards(
                onClick = {
                    state.onEvent(MainScreenEvent.ClickOnWallpaper(it))
                },
                name = rememberString(Strings.exploreNew),
                modifier = modifier.clip(MaterialTheme.shapes.large),
                startItem = {
                    FeaturedWallpapersCarousel(featuredWallpapers,
                        onClick = {
                            state.onEvent(MainScreenEvent.ClickOnWallpaper(it))
                        },
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = MaterialTheme.spacing.extraLarge),
                        onWallpaperRotation = {
                            selectedWallpaper = it
                        })
                },
                wallpapers = state.wallpapers,
                onRandomWallpaper = {
                    state.onEvent(MainScreenEvent.RandomWallpaper)
                },
                backgroundImageBitmap = currentImageAsset?.let { storagedImageBitmap(it) },
                applyNavigationPadding = true
            )
        }
    }
}