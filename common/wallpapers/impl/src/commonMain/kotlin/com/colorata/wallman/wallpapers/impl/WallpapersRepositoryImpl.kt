package com.colorata.wallman.wallpapers.impl

import com.colorata.wallman.categories.api.WallpaperCategory
import com.colorata.wallman.core.data.AppConfiguration
import com.colorata.wallman.core.data.Result
import com.colorata.wallman.core.data.launchIO
import com.colorata.wallman.core.data.module.ApplicationSettings
import com.colorata.wallman.core.data.module.DownloadHandler
import com.colorata.wallman.core.data.module.FileHandler
import com.colorata.wallman.core.data.module.Logger
import com.colorata.wallman.core.data.module.SystemProvider
import com.colorata.wallman.core.data.module.throwable
import com.colorata.wallman.core.data.mutate
import com.colorata.wallman.core.data.onError
import com.colorata.wallman.core.data.runResulting
import com.colorata.wallman.core.data.serialization.Json
import com.colorata.wallman.core.data.util.addIfNotExists
import com.colorata.wallman.wallpapers.WallpaperConfiguration
import com.colorata.wallman.wallpapers.WallpaperI
import com.colorata.wallman.wallpapers.WallpaperPacks
import com.colorata.wallman.wallpapers.WallpapersRepository
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import java.io.File

class WallpapersRepositoryImpl(
    private val downloadHandler: DownloadHandler,
    private val applicationSettings: ApplicationSettings,
    private val systemProvider: SystemProvider,
    private val fileHandler: FileHandler,
    private val scope: CoroutineScope,
    private val logger: Logger
) :
    WallpapersRepository {

    override val wallpapers = MutableStateFlow(listOf<WallpaperI>())

    override val wallpaperConfigurations = MutableStateFlow(listOf<WallpaperConfiguration>())

    override val wallpaperCategories = MutableStateFlow(listOf<WallpaperCategory>())
    override val wallpaperPacks = MutableStateFlow(listOf<WallpaperPacks>())
    private val notForceRefreshResult by lazy {
        _fetchWallpaperConfigurations().shareIn(scope, SharingStarted.Eagerly, replay = 10000)
    }

    private var fetchResult: Flow<Result<Unit>>? = null

    init {
        scope.launchIO({ logger.throwable(it) }) {
            notForceRefreshResult.collect()
        }
    }

    override fun fetchWallpaperConfigurations(forceRefresh: Boolean): Flow<Result<Unit>> {
        return if (!forceRefresh) notForceRefreshResult else {
            if (fetchResult != null) fetchResult!!
            else _fetchWallpaperConfigurations(forceRefresh = true).also {
                fetchResult = it
            }.onCompletion {
                fetchResult = null
            }.shareIn(scope, SharingStarted.Lazily)
        }
    }

    private fun _fetchWallpaperConfigurations(forceRefresh: Boolean = false): Flow<Result<Unit>> {
        val directoryName = "wallpapers-api5"
        return channelFlow {
            if (!forceRefresh) {
                val tryConfigurationResult = convertConfiguration(directoryName)
                if (tryConfigurationResult is Result.Success) {
                    send(tryConfigurationResult)
                    return@channelFlow
                }
            }
            downloadConfigurations(directoryName).collect { result ->
                if (result is Result.Success) {
                    val unzipResult = unzipConfiguration(directoryName)
                    if (unzipResult is Result.Success) {
                        send(convertConfiguration(directoryName))
                    } else send(unzipResult)
                } else send(result)
            }
        }
    }

    private fun convertConfiguration(directoryName: String): Result<Unit> {
        return runResulting {
            val fullPath = systemProvider.filesDirectoryPath + "/$directoryName"
            val configurationPath = "$fullPath/configuration.json"
            val configuration = decodeFromFile<NetWallpaperConfiguration>(configurationPath)
            requireApiMatch(configuration.api)

            val wallpaperPacksPath = "$fullPath/${configuration.packs}"
            val netWallpaperPacks = decodeFromFile<List<NetWallpaperPack>>(wallpaperPacksPath)
            val wallpaperPacks = netWallpaperPacks.map { it.toWallpaperPack() }

            val wallpaperCategoriesPath = "$fullPath/${configuration.categories}"
            val netWallpaperCategories =
                decodeFromFile<List<NetWallpaperCategory>>(wallpaperCategoriesPath)
            val wallpaperCategories = netWallpaperCategories.map { it.toWallpaperCategory() }

            val wallpapersPath = "$fullPath/${configuration.wallpapers}"
            val netWallpapers = decodeFromFile<List<NetWallpaper>>(wallpapersPath)
            val wallpapers =
                netWallpapers.map { it.toWallpaper(netWallpaperCategories, netWallpaperPacks) }
                    .map { wallpaper ->
                        wallpaper.copy(dynamicWallpapers = wallpaper.dynamicWallpapers
                            .map { it.copy(previewRes = "$directoryName/${configuration.resources}/${it.previewRes}") }
                            .toImmutableList(),
                            staticWallpapers = wallpaper.staticWallpapers
                                .map { it.copy(previewRes = "$directoryName/${configuration.resources}/${it.previewRes}") }
                                .toImmutableList()
                        )
                    }

            wallpaperConfigurations.value = wallpaperConfigurations.value.mutate {
                addIfNotExists(
                    WallpaperConfiguration(
                        wallpaperPacks = wallpaperPacks.toImmutableList(),
                        wallpaperCategories = wallpaperCategories.toImmutableList(),
                        wallpapers = wallpapers.toImmutableList()
                    )
                )
            }
            this.wallpaperPacks.value = wallpaperPacks
            this.wallpaperCategories.value = wallpaperCategories
            this.wallpapers.value = wallpapers
        }
    }

    private inline fun <reified T> decodeFromFile(filePath: String): T {
        return Json.decodeFromString(File(filePath).readText())
    }

    private fun requireApiMatch(actualApi: Int) {
        if (actualApi != AppConfiguration.VERSION_CODE) {
            error("This api is not supported, your api: ${AppConfiguration.VERSION_CODE}, downloaded api: $actualApi")
        }
    }

    private suspend fun unzipConfiguration(directoryName: String): Result<Unit> {
        val localPath = systemProvider.filesDirectoryPath + "/$directoryName.zip"
        val unzipPath = systemProvider.filesDirectoryPath + "/$directoryName/"
        return fileHandler.unzip(localPath, unzipPath)
    }

    private fun downloadConfigurations(directoryName: String): Flow<Result<Unit>> {
        return channelFlow {
            val mirror = applicationSettings.settings().value.mirror
            val url = "$mirror/$directoryName.zip"
            val localPath = systemProvider.filesDirectoryPath + "/$directoryName.zip"
            val result = downloadHandler.downloadFile(url, localPath) {
                runBlocking { send(Result.Loading(it)) }
            }
            result.onError {
                send(Result.Error(it))
                return@channelFlow
            }
            send(Result.Success(Unit))
        }
    }
}