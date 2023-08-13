package com.colorata.wallman.categories.viewmodel

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.colorata.wallman.categories.api.CategoryDetailsDestination
import com.colorata.wallman.categories.api.WallpaperCategory
import com.colorata.wallman.core.data.Destinations
import com.colorata.wallman.core.data.DownloadState
import com.colorata.wallman.core.data.launchIO
import com.colorata.wallman.core.data.lazyMolecule
import com.colorata.wallman.core.data.module.Logger
import com.colorata.wallman.core.data.module.NavigationController
import com.colorata.wallman.core.data.module.throwable
import com.colorata.wallman.core.data.util.mapState
import com.colorata.wallman.wallpapers.WallpaperI
import com.colorata.wallman.wallpapers.WallpapersModule
import com.colorata.wallman.wallpapers.WallpapersRepository
import com.colorata.wallman.wallpapers.fetchWallpaperConfigurationsAsDownloadState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

fun WallpapersModule.CategoriesViewModel() =
    CategoriesViewModel(wallpapersRepository, navigationController, logger)

class CategoriesViewModel(
    private val repo: WallpapersRepository,
    private val navigation: NavigationController,
    private val logger: Logger,
) : ViewModel() {
    private val categories =
        repo.wallpaperCategories.mapState(viewModelScope) { it.toImmutableList() }

    private val downloadState = MutableStateFlow<DownloadState>(DownloadState.NotConnected)
    private val isRefreshing = MutableStateFlow(false)
    private fun onClickCategoryCard(index: Int) {
        navigation.navigate(Destinations.CategoryDetailsDestination(index))
    }

    private var initJob: Job? = null

    init {
        initJob = viewModelScope.launchIO({ logger.throwable(it) }) {
            isRefreshing.value = true
            repo.fetchWallpaperConfigurationsAsDownloadState(forceRefresh = false).collect {
                downloadState.value = it
                if (it !is DownloadState.Downloading && it !is DownloadState.Connecting) isRefreshing.value =
                    false
            }
        }
    }

    private fun refresh() {
        initJob?.cancel()
        viewModelScope.launchIO({ logger.throwable(it) }) {
            isRefreshing.value = true
            repo.fetchWallpaperConfigurationsAsDownloadState(forceRefresh = true).collect {
                downloadState.value = it
            }
        }.invokeOnCompletion {
            viewModelScope.launch {
                delay(1000)
                isRefreshing.value = false
            }
        }
    }

    val state by lazyMolecule {
        val categories by categories.collectAsState()
        val wallpapers by repo.wallpapers.collectAsState()
        val downloadState by downloadState.collectAsState()
        val isRefreshing by isRefreshing.collectAsState()
        CategoriesScreenState(
            categories = categories.toImmutableList(),
            wallpapers = wallpapers.toImmutableList(),
            downloadState = downloadState,
            isRefreshing = isRefreshing
        ) { event ->
            when (event) {
                is CategoriesScreenEvent.ClickOnCategory -> onClickCategoryCard(event.index)
                CategoriesScreenEvent.Refresh -> refresh()
            }
        }
    }

    data class CategoriesScreenState(
        val categories: ImmutableList<WallpaperCategory>,
        val wallpapers: ImmutableList<WallpaperI>,
        val downloadState: DownloadState,
        val isRefreshing: Boolean = false,
        val onEvent: (CategoriesScreenEvent) -> Unit
    )

    @Immutable
    sealed interface CategoriesScreenEvent {
        data class ClickOnCategory(val index: Int) : CategoriesScreenEvent

        data object Refresh : CategoriesScreenEvent
    }
}