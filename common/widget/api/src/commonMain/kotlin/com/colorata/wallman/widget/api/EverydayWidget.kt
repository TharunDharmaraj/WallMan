package com.colorata.wallman.widget.api

import com.colorata.wallman.wallpapers.DynamicWallpaper
import com.colorata.wallman.wallpapers.WallpaperI

interface EverydayWidget {

    companion object {
        val shapeKey = "shape"
        val wallpaper = "wallpaper"
        val wallpaperHashcode = "wallpaperHashcode"

        fun List<WallpaperI>.getDynamicWallpaperByHashCode(hashCode: Int): DynamicWallpaper? {
            val wallpaper =
                find { wallpaper -> wallpaper.dynamicWallpapers.any { it.hashCode() == hashCode } }
                    ?: return null

            return wallpaper.dynamicWallpapers.first { it.hashCode() == hashCode }
        }
    }
}