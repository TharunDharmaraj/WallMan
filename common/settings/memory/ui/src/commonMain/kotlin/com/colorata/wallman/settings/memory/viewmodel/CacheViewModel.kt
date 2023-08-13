package com.colorata.wallman.settings.memory.viewmodel

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.colorata.wallman.core.data.lazyMolecule
import com.colorata.wallman.wallpapers.WallpaperManager
import com.colorata.wallman.wallpapers.WallpaperPacks
import com.colorata.wallman.wallpapers.WallpapersModule
import com.colorata.wallman.wallpapers.WallpapersRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

fun WallpapersModule.CacheViewModel() = CacheViewModel(wallpaperManager, wallpapersRepository)

class CacheViewModel(
    private val wallpaperManager: WallpaperManager,
    repository: WallpapersRepository
) :
    ViewModel() {

    private fun onCacheClick(pack: WallpaperPacks) {
        viewModelScope.launch {
            val result = wallpaperManager.deleteWallpaperPackCache(pack)
        }
    }

    private fun onDeleteClick(pack: WallpaperPacks) {
        viewModelScope.launch {
            wallpaperManager.deleteWallpaperPack(pack)
        }
    }

    private val downloadedWallpaperPacks =
        wallpaperManager.cachedWallpaperPacks()

    private val installedWallpaperPacks =
        wallpaperManager.installedWallpaperPacks()

    private val wallpaperPacks = repository.wallpaperPacks

    val state by lazyMolecule {
        val downloadedPacks by downloadedWallpaperPacks.collectAsState()
        val installedPacks by installedWallpaperPacks.collectAsState()
        val wallpaperPacks by wallpaperPacks.collectAsState()
        return@lazyMolecule CacheScreenState(
            wallpaperPacks.toImmutableList(),
            downloadedPacks.toImmutableList(),
            installedPacks.toImmutableList(),
        ) { event ->
            when (event) {
                is CacheScreenEvent.ClearCache -> onCacheClick(event.pack)
                is CacheScreenEvent.DeletePack -> onDeleteClick(event.pack)
            }
        }
    }

    data class CacheScreenState(
        val wallpaperPacks: ImmutableList<WallpaperPacks>,
        val downloadedWallpaperPacks: ImmutableList<WallpaperPacks>,
        val installedWallpaperPacks: ImmutableList<WallpaperPacks>,
        val onEvent: (CacheScreenEvent) -> Unit
    )

    @Immutable
    sealed interface CacheScreenEvent {
        data class ClearCache(val pack: WallpaperPacks) : CacheScreenEvent
        data class DeletePack(val pack: WallpaperPacks) : CacheScreenEvent
    }
}