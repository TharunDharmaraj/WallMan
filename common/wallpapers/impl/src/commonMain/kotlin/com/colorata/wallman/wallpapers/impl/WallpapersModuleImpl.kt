package com.colorata.wallman.wallpapers.impl

import android.app.Application
import com.colorata.wallman.core.data.module.CoreModule
import com.colorata.wallman.wallpapers.WallpaperManager
import com.colorata.wallman.wallpapers.WallpaperProvider
import com.colorata.wallman.wallpapers.WallpapersModule
import com.colorata.wallman.wallpapers.WallpapersRepository

class WallpapersModuleImpl(
    coreModule: CoreModule,
    application: Application
) : WallpapersModule,
    CoreModule by coreModule {
    override val wallpaperProvider: WallpaperProvider by lazy {
        WallpaperProviderImpl(application, coroutineScope, logger)
    }
    override val wallpapersRepository: WallpapersRepository by lazy {
        WallpapersRepositoryImpl(
            downloadHandler,
            applicationSettings,
            systemProvider,
            fileHandler,
            coroutineScope,
            logger
        )
    }
    override val wallpaperManager: WallpaperManager by lazy {
        WallpaperManagerImpl(
            systemProvider,
            appsProvider,
            downloadHandler,
            applicationSettings,
            wallpaperProvider,
            coroutineScope,
            wallpapersRepository
        )
    }
}