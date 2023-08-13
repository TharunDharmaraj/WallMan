package com.colorata.wallman.core.di

import androidx.compose.runtime.compositionLocalOf
import com.colorata.wallman.core.data.module.CoreModule
import com.colorata.wallman.wallpapers.WallpapersModule
import com.colorata.wallman.widget.api.WidgetModule

interface Graph {
    val coreModule: CoreModule
    val wallpapersModule: WallpapersModule
    val widgetModule: WidgetModule
}

val LocalGraph = compositionLocalOf<Graph> { error("No Graph provided") }