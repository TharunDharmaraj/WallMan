package com.colorata.wallman.wallpapers

import com.colorata.wallman.categories.api.WallpaperCategory

fun WallpaperCategory.categoryWallpapers(wallpapers: List<WallpaperI>) = wallpapers.filter { it.category == this }
