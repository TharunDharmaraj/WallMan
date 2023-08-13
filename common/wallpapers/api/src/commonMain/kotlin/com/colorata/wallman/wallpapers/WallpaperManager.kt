package com.colorata.wallman.wallpapers

import com.colorata.wallman.core.data.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface WallpaperManager {
    fun installedWallpaperPacks(): StateFlow<List<WallpaperPacks>>

    fun cachedWallpaperPacks(): StateFlow<List<WallpaperPacks>>

    suspend fun installWallpaperPack(pack: WallpaperPacks): Result<Unit>

    suspend fun deleteWallpaperPack(pack: WallpaperPacks): Result<Unit>

    fun downloadWallpaperPack(pack: WallpaperPacks): Flow<Result<Unit>>

    fun resultForDownloadWallpaperPack(pack: WallpaperPacks): Flow<Result<Unit>>?

    fun deleteWallpaperPackCache(pack: WallpaperPacks): Result<Unit>

    fun stopDownloadingWallpaperPack(pack: WallpaperPacks)

    fun installStaticWallpaper(wallpaper: StaticWallpaper, variant: StaticWallpaperApplyVariant): Flow<Result<Unit>>

    fun currentlyInstalledDynamicWallpaper(): Flow<WallpaperProvider.LiveWallpaper?>
}