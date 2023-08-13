package com.colorata.wallman.wallpapers.impl

import com.colorata.wallman.core.data.Result
import com.colorata.wallman.core.data.module.ApplicationSettings
import com.colorata.wallman.core.data.module.AppsProvider
import com.colorata.wallman.core.data.module.DownloadHandler
import com.colorata.wallman.core.data.module.SystemProvider
import com.colorata.wallman.core.data.mutate
import com.colorata.wallman.core.data.runResulting
import com.colorata.wallman.wallpapers.StaticWallpaper
import com.colorata.wallman.wallpapers.StaticWallpaperApplyVariant
import com.colorata.wallman.wallpapers.WallpaperManager
import com.colorata.wallman.wallpapers.WallpaperPacks
import com.colorata.wallman.wallpapers.WallpaperProvider
import com.colorata.wallman.wallpapers.WallpapersRepository
import com.colorata.wallman.wallpapers.fullUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.io.File

class WallpaperManagerImpl(
    private val systemProvider: SystemProvider,
    private val appsProvider: AppsProvider,
    private val downloadHandler: DownloadHandler,
    applicationSettings: ApplicationSettings,
    private val wallpaperProvider: WallpaperProvider,
    private val scope: CoroutineScope,
    wallpapersRepository: WallpapersRepository
) : WallpaperManager {
    private val settings = applicationSettings.settings()
    private val wallpaperPacks = wallpapersRepository.wallpaperPacks
    private val cacheStorage by lazy { systemProvider.externalCacheDirectoryPath }
    private val _installedWallpaperPacks by lazy {
        appsProvider.installedApps().map { apps ->
            apps.filter { packageName -> wallpaperPacks.value.any { it.packageName == packageName } }
                .map { packageName -> wallpaperPacks.value.first { it.packageName == packageName } }
        }.stateIn(scope, SharingStarted.Eagerly, initialValue = listOf())
    }

    private val _downloadingWallpaperPacks by lazy { mutableMapOf<WallpaperPacks, Flow<Result<Unit>>>() }

    private val _cachedWallpaperPacks by lazy {
        MutableStateFlow(getCachedWallpapers())
    }

    override fun installedWallpaperPacks(): StateFlow<List<WallpaperPacks>> {
        return _installedWallpaperPacks
    }

    override fun cachedWallpaperPacks(): StateFlow<List<WallpaperPacks>> {
        return _cachedWallpaperPacks
    }

    override suspend fun installWallpaperPack(pack: WallpaperPacks): Result<Unit> {
        return appsProvider.installApp(path = cacheStorage + "/" + pack.url)
    }

    override suspend fun deleteWallpaperPack(pack: WallpaperPacks): Result<Unit> {
        return appsProvider.deleteApp(packageName = pack.packageName)
    }

    override fun downloadWallpaperPack(pack: WallpaperPacks): Flow<Result<Unit>> {
        return _downloadingWallpaperPacks.getOrPut(pack) {
            downloadHandler.downloadFileInBackground(
                settings.value.mirror + pack.url,
                cacheStorage + "/" + pack.url,
                pack.description.value
            ).onEach { result ->
                if (result !is Result.Loading) _downloadingWallpaperPacks.remove(pack)
                if (result is Result.Success) _cachedWallpaperPacks.value += pack
                else if (result is Result.Error) _cachedWallpaperPacks.update {
                    it.mutate { remove(pack) }
                }
            }.stateIn(scope, SharingStarted.Lazily, Result.Loading(0f))
        }
    }

    override fun resultForDownloadWallpaperPack(pack: WallpaperPacks): Flow<Result<Unit>>? {
        return _downloadingWallpaperPacks[pack]
    }

    override fun deleteWallpaperPackCache(pack: WallpaperPacks): Result<Unit> {
        return runResulting {
            File(cacheStorage + "/" + pack.url).delete()
            _cachedWallpaperPacks.update { it.mutate { remove(pack) } }
        }
    }

    override fun stopDownloadingWallpaperPack(pack: WallpaperPacks) {
        downloadHandler.stopDownloadingFileInBackground(
            settings.value.mirror + pack.url,
            cacheStorage + "/" + pack.url,
        )
    }

    override fun installStaticWallpaper(wallpaper: StaticWallpaper, variant: StaticWallpaperApplyVariant): Flow<Result<Unit>> {
        val subPath = wallpaper.fullUrl()
        return flow {
            emit(Result.Loading(0f))
            val localPath = "$cacheStorage/$subPath"
            val downloadResult = downloadHandler.downloadFile(
                settings.value.mirror + subPath,
                localPath
            )
            if (downloadResult is Result.Error) {
                emit(downloadResult)
                return@flow
            }
            wallpaperProvider.installStaticWallpaper(localPath, variant).collect {
                emit(it)
            }
        }
    }

    override fun currentlyInstalledDynamicWallpaper(): Flow<WallpaperProvider.LiveWallpaper?> {
        return wallpaperProvider.currentLiveWallpaper()
    }

    private fun getCachedWallpapers(): List<WallpaperPacks> {
        return File(cacheStorage).listFiles()?.filter { file ->
            wallpaperPacks.value.any {
                it.url == file.name
            }
        }?.map { file ->
            wallpaperPacks.value.first { it.url == file.name }
        }?.filter { pack ->
            val fileLength =
                File(cacheStorage + "/" + pack.url).length()
            fileLength == pack.checksum
        } ?: listOf()
    }
}