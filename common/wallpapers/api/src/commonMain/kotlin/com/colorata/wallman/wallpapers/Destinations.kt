package com.colorata.wallman.wallpapers

import com.colorata.wallman.core.data.Destinations
import com.colorata.wallman.core.data.destination
import com.colorata.wallman.core.data.withArgument

fun Destinations.MainDestination() = destination("Main")
fun Destinations.WallpaperDetailsDestination(wallpaperIndex: Int? = null) = destination(
    "Wallpaper/{index}", "Wallpaper/"
).withArgument(
    wallpaperIndex, argumentName = "index", defaultValue = ""
) { it }