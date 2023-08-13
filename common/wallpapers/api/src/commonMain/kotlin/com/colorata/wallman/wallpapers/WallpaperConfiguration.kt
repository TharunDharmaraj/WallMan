package com.colorata.wallman.wallpapers

import com.colorata.wallman.categories.api.WallpaperCategory
import kotlinx.collections.immutable.ImmutableList

data class WallpaperConfiguration(
    val wallpaperPacks: ImmutableList<WallpaperPacks>,
    val wallpaperCategories: ImmutableList<WallpaperCategory>,
    val wallpapers: ImmutableList<WallpaperI>
)