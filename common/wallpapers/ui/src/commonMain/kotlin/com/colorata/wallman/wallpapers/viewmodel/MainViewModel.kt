package com.colorata.wallman.wallpapers.viewmodel

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.colorata.wallman.core.data.Destinations
import com.colorata.wallman.core.data.DownloadState
import com.colorata.wallman.core.data.launchIO
import com.colorata.wallman.core.data.lazyMolecule
import com.colorata.wallman.core.data.module.Logger
import com.colorata.wallman.core.data.module.NavigationController
import com.colorata.wallman.core.data.module.throwable
import com.colorata.wallman.core.data.util.takeLastOrLess
import com.colorata.wallman.wallpapers.WallpaperDetailsDestination
import com.colorata.wallman.wallpapers.WallpaperI
import com.colorata.wallman.wallpapers.WallpapersModule
import com.colorata.wallman.wallpapers.WallpapersRepository
import com.colorata.wallman.wallpapers.fetchWallpaperConfigurationsAsDownloadState
import com.colorata.wallman.wallpapers.indexOfWallpaper
import com.colorata.wallman.wallpapers.randomWallpaperIndex
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow

fun WallpapersModule.MainViewModel() =
    MainViewModel(wallpapersRepository, navigationController, logger)

class MainViewModel(
    private val repo: WallpapersRepository,
    private val navigation: NavigationController,
    private val logger: Logger
) : ViewModel() {

    private val isRefreshing = MutableStateFlow(false)
    private fun goToRandomWallpaper() {
        navigation.navigate(
            Destinations.WallpaperDetailsDestination(repo.randomWallpaperIndex())
        )
    }

    private val refreshState = MutableStateFlow<DownloadState>(DownloadState.NotConnected)

    private var initJob: Job? = null

    init {
        initJob = viewModelScope.launchIO({ logger.throwable(it) }) {
            isRefreshing.value = true
            repo.fetchWallpaperConfigurationsAsDownloadState(forceRefresh = false).collect {
                refreshState.value = it
                if (it !is DownloadState.Downloading && it !is DownloadState.Connecting)
                    isRefreshing.value = false
            }
        }
    }

    private fun onWallpaperClick(wallpaperIndex: Int) =
        navigation.navigate(Destinations.WallpaperDetailsDestination(wallpaperIndex))

    private fun refresh() {
        initJob?.cancel()
        viewModelScope.launchIO({ logger.throwable(it) }) {
            isRefreshing.value = true
            repo.fetchWallpaperConfigurationsAsDownloadState(forceRefresh = true).collect {
                refreshState.value = it
                if (it is DownloadState.Error) logger.throwable(it.throwable)
            }
            delay(1000)
            isRefreshing.value = false
        }
    }

    val state by lazyMolecule {
        val wallpapers by repo.wallpapers.collectAsState()
        val featuredWallpapers =
            remember(wallpapers) { wallpapers.takeLastOrLess(15).toImmutableList() }
        val isRefreshing by isRefreshing.collectAsState()
        val refreshState by refreshState.collectAsState()
        MainScreenState(
            wallpapers = wallpapers.toImmutableList(),
            featuredWallpapers = featuredWallpapers,
            refreshing = isRefreshing,
            refreshState = refreshState
        ) {
            when (it) {
                is MainScreenEvent.RandomWallpaper -> goToRandomWallpaper()
                is MainScreenEvent.ClickOnWallpaper -> onWallpaperClick(repo.indexOfWallpaper(it.wallpaper))
                MainScreenEvent.Refresh -> refresh()
            }
        }
    }

    data class MainScreenState(
        val wallpapers: ImmutableList<WallpaperI>,
        val featuredWallpapers: ImmutableList<WallpaperI>,
        val refreshing: Boolean,
        val refreshState: DownloadState,
        val onEvent: (MainScreenEvent) -> Unit
    )
}

@Immutable
sealed interface MainScreenEvent {
    data object RandomWallpaper : MainScreenEvent
    data class ClickOnWallpaper(val wallpaper: WallpaperI) : MainScreenEvent

    data object Refresh : MainScreenEvent
}
