package com.colorata.wallman.wallpapers

import com.colorata.wallman.categories.api.WallpaperCategory
import com.colorata.wallman.core.data.DownloadState
import com.colorata.wallman.core.data.Result
import com.colorata.wallman.core.data.onError
import com.colorata.wallman.core.data.onLoading
import com.colorata.wallman.core.data.onSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

interface WallpapersRepository {

    val wallpapers: StateFlow<List<WallpaperI>>

    val wallpaperConfigurations: StateFlow<List<WallpaperConfiguration>>

    val wallpaperCategories: StateFlow<List<WallpaperCategory>>

    val wallpaperPacks: StateFlow<List<WallpaperPacks>>

    fun fetchWallpaperConfigurations(forceRefresh: Boolean = false): Flow<Result<Unit>>
}

fun WallpapersRepository.wallpapersForCategory(
    category: WallpaperCategory
): Flow<List<WallpaperI>> =
    wallpapers.map { it.filter { wallpaper -> wallpaper.category == category } }

fun WallpapersRepository.indexOfWallpaper(wallpaper: WallpaperI): Int =
    wallpapers.value.indexOf(wallpaper)

suspend fun WallpapersRepository.waitForWallpapersLoad() {
    wallpapers.first { it.isNotEmpty() }
}
fun WallpapersRepository.randomWallpaper() = wallpapers.value.random()

fun WallpapersRepository.randomWallpaperIndex() = wallpapers.value.indices.random()

fun WallpapersRepository.randomWallpaperIndexForCategory(category: WallpaperCategory) =
    wallpapers.value.withIndex().filter { it.value.category.id == category.id }.random().index

fun WallpapersRepository.fetchWallpaperConfigurationsAsDownloadState(forceRefresh: Boolean): Flow<DownloadState> {
    return channelFlow {
        send(DownloadState.Connecting)
        fetchWallpaperConfigurations(forceRefresh).collect { result ->
            result.onLoading {
                send(DownloadState.Downloading(it))
            }.onError {
                send(DownloadState.Error(it))
            }.onSuccess {
                send(DownloadState.Downloaded)
            }
        }
    }
}