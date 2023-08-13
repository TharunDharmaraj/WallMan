package com.colorata.wallman.wallpapers.viewmodel

import androidx.compose.runtime.Immutable
import com.colorata.wallman.wallpapers.BaseWallpaper
import com.colorata.wallman.wallpapers.DynamicWallpaper
import com.colorata.wallman.wallpapers.WallpaperI
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class WallpaperDetailsScreenState(
    val wallpaper: WallpaperI,
    val selectedWallpaper: BaseWallpaper,
    val wallpaperVariants: ImmutableList<BaseWallpaper>,
    val downloadProgress: Float,
    val cacheState: DynamicWallpaper.DynamicWallpaperCacheState,
    val selectedWallpaperType: WallpaperI.SelectedWallpaperType,
    val actionType: WallpaperI.ActionType,
    val showPermissionRequest: Boolean = false,
    val showPerformanceWarning: Boolean = false,
    val showStaticWallpaperChooser: Boolean = false,
    val onEvent: (WallpaperDetailsScreenEvent) -> Unit
)