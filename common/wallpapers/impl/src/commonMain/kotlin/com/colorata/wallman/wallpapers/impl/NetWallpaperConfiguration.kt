package com.colorata.wallman.wallpapers.impl

import kotlinx.serialization.Serializable

@Serializable
data class NetWallpaperConfiguration(
    val api: Int,
    val categories: String,
    val packs: String,
    val wallpapers: String,
    val resources: String,
    val urls: List<String>
)