package com.colorata.wallman.categories.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.colorata.wallman.categories.api.CategoryDetailsDestination
import com.colorata.wallman.categories.viewmodel.CategoryDetailsViewModel
import com.colorata.wallman.core.data.Destinations
import com.colorata.wallman.core.data.MaterialNavGraphBuilder
import com.colorata.wallman.core.data.continuousComposable
import com.colorata.wallman.core.data.parameter
import com.colorata.wallman.core.data.rememberString
import com.colorata.wallman.core.data.viewModel
import com.colorata.wallman.wallpapers.WallpapersModule
import com.colorata.wallman.wallpapers.ui.components.FilteredWallpaperCards

context(WallpapersModule)
fun MaterialNavGraphBuilder.categoryDetailsScreen() {
    continuousComposable(Destinations.CategoryDetailsDestination()) {
        val index by parameter()
        CategoryDetailsScreen(categoryIndex = index?.toInt() ?: 0)
    }
}

context(WallpapersModule)
@Composable
fun CategoryDetailsScreen(categoryIndex: Int, modifier: Modifier = Modifier) {
    val viewModel = viewModel { CategoryDetailsViewModel(categoryIndex) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    CategoryDetailsScreen(state, modifier)
}

@Composable
private fun CategoryDetailsScreen(
    state: CategoryDetailsViewModel.CategoryDetailsScreenState,
    modifier: Modifier = Modifier
) {
    FilteredWallpaperCards(
        onClick = {
            state.onEvent(CategoryDetailsViewModel.CategoryDetailsScreenEvent.GoToWallpaper(it))
        },
        rememberString(state.category.locale.name),
        modifier,
        description = rememberString(state.category.locale.description),
        wallpapers = state.wallpapers,
        onRandomWallpaper = {
            state.onEvent(CategoryDetailsViewModel.CategoryDetailsScreenEvent.GoToRandomWallpaper)
        }
    )
}