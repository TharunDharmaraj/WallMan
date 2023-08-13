package com.colorata.wallman.categories.api

import com.colorata.wallman.core.data.Strings


data class WallpaperCategory(val locale: WallpaperCategoryLocale, val id: Int) {
    companion object {
        val Appulse = WallpaperCategory(Strings.appulse, 0)
        val Wonders = WallpaperCategory(Strings.wonders, 1)
        val Peaceful = WallpaperCategory(Strings.peaceful, 2)
        val Fancy = WallpaperCategory(Strings.fancy, 3)
        val Garden = WallpaperCategory(Strings.garden, 4)
        val Birdies = WallpaperCategory(Strings.birdies, 5)
    }
}