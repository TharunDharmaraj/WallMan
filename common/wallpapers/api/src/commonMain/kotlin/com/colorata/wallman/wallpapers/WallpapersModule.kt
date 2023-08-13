package com.colorata.wallman.wallpapers

import com.colorata.wallman.core.data.module.CoreModule

interface WallpapersModule : CoreModule {
    val wallpaperProvider: WallpaperProvider

    val wallpaperManager: WallpaperManager

    val wallpapersRepository: WallpapersRepository
}
