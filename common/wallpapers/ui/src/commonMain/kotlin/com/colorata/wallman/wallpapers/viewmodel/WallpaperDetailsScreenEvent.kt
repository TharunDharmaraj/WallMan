package com.colorata.wallman.wallpapers.viewmodel

import androidx.compose.runtime.Immutable
import com.colorata.wallman.wallpapers.BaseWallpaper
import com.colorata.wallman.wallpapers.StaticWallpaperApplyVariant
import com.colorata.wallman.wallpapers.WallpaperI

@Immutable
sealed interface WallpaperDetailsScreenEvent {
    data object ClickOnActionButton : WallpaperDetailsScreenEvent
    data object ClickOnDownload : WallpaperDetailsScreenEvent

    data object GoToMaps : WallpaperDetailsScreenEvent

    data class SelectWallpaperType(val type: WallpaperI.SelectedWallpaperType) :
        WallpaperDetailsScreenEvent

    data class SelectBaseWallpaper(val wallpaper: BaseWallpaper) : WallpaperDetailsScreenEvent

    data object DismissPermissionRequest : WallpaperDetailsScreenEvent

    data object GoToInstallAppsPermissionsPage : WallpaperDetailsScreenEvent

    data object ProceedPerformanceWarning : WallpaperDetailsScreenEvent
    data object DismissPerformanceWarning : WallpaperDetailsScreenEvent

    data class ChooseStaticWallpaperType(val variant: StaticWallpaperApplyVariant): WallpaperDetailsScreenEvent

    data object DismissStaticWallpaperChooser: WallpaperDetailsScreenEvent
}