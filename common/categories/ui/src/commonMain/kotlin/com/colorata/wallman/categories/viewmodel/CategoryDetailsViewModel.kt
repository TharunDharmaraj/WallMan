package com.colorata.wallman.categories.viewmodel

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.colorata.wallman.categories.api.WallpaperCategory
import com.colorata.wallman.core.data.Destinations
import com.colorata.wallman.core.data.lazyMolecule
import com.colorata.wallman.core.data.module.NavigationController
import com.colorata.wallman.core.data.util.mapState
import com.colorata.wallman.wallpapers.WallpaperDetailsDestination
import com.colorata.wallman.wallpapers.WallpaperI
import com.colorata.wallman.wallpapers.WallpapersModule
import com.colorata.wallman.wallpapers.WallpapersRepository
import com.colorata.wallman.wallpapers.indexOfWallpaper
import com.colorata.wallman.wallpapers.randomWallpaperIndexForCategory
import com.colorata.wallman.wallpapers.waitForWallpapersLoad
import com.colorata.wallman.wallpapers.wallpapersForCategory
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking

fun WallpapersModule.CategoryDetailsViewModel(index: Int) =
    CategoryDetailsViewModel(wallpapersRepository, navigationController, index)

class CategoryDetailsViewModel(
    private val repo: WallpapersRepository,
    private val navigation: NavigationController,
    index: Int
) : ViewModel() {

    private val category by lazy {
        runBlocking {
            repo.waitForWallpapersLoad()
            repo.wallpaperCategories.mapState(viewModelScope) { it.toImmutableList()[index] }
        }
    }
    private val wallpapers = repo.wallpapersForCategory(category.value)
        .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    private fun goToWallpaper(index: Int) {
        navigation.navigate(Destinations.WallpaperDetailsDestination(index))
    }

    private fun goToRandomWallpaper() {
        navigation.navigate(
            Destinations.WallpaperDetailsDestination(
                repo.randomWallpaperIndexForCategory(category.value)
            )
        )
    }

    val state by lazyMolecule {
        val category by category.collectAsState()
        val wallpapers by wallpapers.collectAsState()
        CategoryDetailsScreenState(
            wallpapers.toImmutableList(),
            category
        ) { event ->
            when (event) {
                is CategoryDetailsScreenEvent.GoToWallpaper -> goToWallpaper(
                    repo.indexOfWallpaper(event.wallpaper)
                )

                is CategoryDetailsScreenEvent.GoToRandomWallpaper -> goToRandomWallpaper()
            }
        }
    }

    data class CategoryDetailsScreenState(
        val wallpapers: ImmutableList<WallpaperI>,
        val category: WallpaperCategory,
        val onEvent: (CategoryDetailsScreenEvent) -> Unit
    )

    @Immutable
    sealed interface CategoryDetailsScreenEvent {
        data object GoToRandomWallpaper : CategoryDetailsScreenEvent
        data class GoToWallpaper(val wallpaper: WallpaperI) : CategoryDetailsScreenEvent
    }
}