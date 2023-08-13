package com.colorata.wallman.wallpapers

import com.colorata.wallman.core.data.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface WallpaperProvider {
    data class LiveWallpaper(
        val packageName: String,
        val serviceName: String
    )

    fun currentLiveWallpaper(): Flow<LiveWallpaper?>

    fun installStaticWallpaper(path: String, variant: StaticWallpaperApplyVariant): Flow<Result<Unit>>

    object NoopWallpaperProvider : WallpaperProvider {
        override fun currentLiveWallpaper(): Flow<LiveWallpaper?> {
            return flow { }
        }

        override fun installStaticWallpaper(path: String, variant: StaticWallpaperApplyVariant): Flow<Result<Unit>> {
            return flow { }
        }
    }
}