package com.colorata.wallman.wallpapers.impl

import android.annotation.SuppressLint
import android.app.WallpaperInfo
import android.app.WallpaperManager
import android.content.Context
import android.graphics.BitmapFactory
import com.colorata.wallman.core.data.Result
import com.colorata.wallman.core.data.launchIO
import com.colorata.wallman.core.data.module.Logger
import com.colorata.wallman.core.data.module.throwable
import com.colorata.wallman.core.data.runResulting
import com.colorata.wallman.wallpapers.StaticWallpaperApplyVariant
import com.colorata.wallman.wallpapers.WallpaperProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive

class WallpaperProviderImpl(
    private val context: Context,
    scope: CoroutineScope,
    private val logger: Logger
) : WallpaperProvider {
    private val wallpaperManager by lazy { WallpaperManager.getInstance(context) }
    private val _currentLiveWallpaper = MutableStateFlow<WallpaperProvider.LiveWallpaper?>(null)

    init {
        scope.launchIO({ logger.throwable(it) }) {
            while (isActive) {
                _currentLiveWallpaper.value = wallpaperManager.wallpaperInfo?.toLiveWallpaper()
                delay(1000)
            }
        }
    }

    override fun currentLiveWallpaper(): Flow<WallpaperProvider.LiveWallpaper?> {
        return _currentLiveWallpaper
    }

    @SuppressLint("MissingPermission")
    override fun installStaticWallpaper(
        path: String,
        variant: StaticWallpaperApplyVariant
    ): Flow<Result<Unit>> {
        return flow {
            runResulting {
                val bitmap = BitmapFactory.decodeFile(path)
                if (variant == StaticWallpaperApplyVariant.LockScreen || variant == StaticWallpaperApplyVariant.Both)
                    wallpaperManager.setBitmap(
                        /* fullImage = */ bitmap,
                        /* visibleCropHint = */ null,
                        /* allowBackup = */ true,
                        /* which = */ WallpaperManager.FLAG_LOCK
                    )

                if (variant == StaticWallpaperApplyVariant.HomeScreen || variant == StaticWallpaperApplyVariant.Both)
                    wallpaperManager.setBitmap(
                        /* fullImage = */ bitmap,
                        /* visibleCropHint = */ null,
                        /* allowBackup = */ true,
                        /* which = */ WallpaperManager.FLAG_SYSTEM
                    )
            }
        }
    }

    private fun WallpaperInfo.toLiveWallpaper() =
        WallpaperProvider.LiveWallpaper(packageName, serviceName)
}