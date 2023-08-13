package com.colorata.wallman.greeting.viewmodel

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.colorata.wallman.core.data.Destinations
import com.colorata.wallman.core.data.DownloadState
import com.colorata.wallman.core.data.launchIO
import com.colorata.wallman.core.data.launchMain
import com.colorata.wallman.core.data.lazyMolecule
import com.colorata.wallman.core.data.module.ApplicationSettings
import com.colorata.wallman.core.data.module.Logger
import com.colorata.wallman.core.data.module.NavigationController
import com.colorata.wallman.core.data.module.throwable
import com.colorata.wallman.wallpapers.MainDestination
import com.colorata.wallman.wallpapers.WallpapersModule
import com.colorata.wallman.wallpapers.WallpapersRepository
import com.colorata.wallman.wallpapers.fetchWallpaperConfigurationsAsDownloadState
import kotlinx.coroutines.flow.MutableStateFlow

fun WallpapersModule.GreetingViewModel() =
    GreetingViewModel(navigationController, wallpapersRepository, logger, applicationSettings)

class GreetingViewModel(
    private val navigationController: NavigationController,
    private val repo: WallpapersRepository,
    private val logger: Logger,
    private val applicationSettings: ApplicationSettings
) : ViewModel() {

    private val downloadState = MutableStateFlow<DownloadState>(DownloadState.NotConnected)

    init {
        viewModelScope.launchMain({ logger.throwable(it)}) {
            if (applicationSettings.settings().value.proceededGreeting) {
                navigationController.resetRootTo(Destinations.MainDestination())
            }
            fetchWallpapers()
        }
    }

    private fun goToMainScreen() {
        applicationSettings.mutate {
            it.copy(proceededGreeting = true)
        }
        navigationController.resetRootTo(Destinations.MainDestination())
    }

    private fun fetchWallpapers(forceRefresh: Boolean = false) {
        viewModelScope.launchIO({
            downloadState.value = DownloadState.Error(it)
            logger.throwable(it)
        }) {
            repo.fetchWallpaperConfigurationsAsDownloadState(forceRefresh).collect {
                downloadState.value = it
                if (it is DownloadState.Error) logger.throwable(it.throwable)
            }
        }
    }

    val state by lazyMolecule {
        val downloadState by downloadState.collectAsState()
        return@lazyMolecule GreetingScreenState(downloadState) { event ->
            when (event) {
                GreetingScreenEvent.Continue -> goToMainScreen()
                GreetingScreenEvent.Retry -> fetchWallpapers(forceRefresh = true)
            }
        }
    }
}

data class GreetingScreenState(
    val downloadState: DownloadState,
    val onEvent: (GreetingScreenEvent) -> Unit
)

@Immutable
sealed interface GreetingScreenEvent {
    data object Continue : GreetingScreenEvent

    data object Retry : GreetingScreenEvent
}