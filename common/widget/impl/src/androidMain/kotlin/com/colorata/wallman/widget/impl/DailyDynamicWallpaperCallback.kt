package com.colorata.wallman.widget.impl

import android.content.Context
import androidx.annotation.Keep
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.colorata.wallman.core.di.graph
import com.colorata.wallman.wallpapers.goToLiveWallpaper
import com.colorata.wallman.wallpapers.supportsDynamicWallpapers
import com.colorata.wallman.widget.api.EverydayWidget
import com.colorata.wallman.widget.api.WidgetState
import com.colorata.wallman.widget.ui_widget.EverydayWidgetContent

@Keep
class DailyDynamicWallpaperCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val graph = context.graph
        val intentHandler = graph.coreModule.intentHandler
        val mainRepo = graph.wallpapersModule.wallpapersRepository
        val widget = WidgetState(context, PreferencesGlanceStateDefinition, glanceId)
        widget.update(
            createEverydayWidgetContent()
        ) {
            val currentWallpaperHashcode =
                this[intPreferencesKey(EverydayWidget.wallpaperHashcode)]
            if (currentWallpaperHashcode != null) {
                val wallpaper = EverydayWidget.run {
                    mainRepo.wallpapers.value.toList()
                        .getDynamicWallpaperByHashCode(currentWallpaperHashcode)
                }
                if (wallpaper != null) intentHandler.goToLiveWallpaper(wallpaper)
            }
            updateWallpaper(context)
        }
    }

}

internal fun createEverydayWidgetContent() =
    EverydayWidgetContent(
        actionRunCallback<DailyDynamicWallpaperCallback>()
    )

internal fun MutablePreferences.updateWallpaper(context: Context) {
    val mainRepo = context.graph.wallpapersModule.wallpapersRepository
    val randomWallpaper =
        mainRepo.wallpapers.value.filter { it.supportsDynamicWallpapers() }.random()
    this[intPreferencesKey(EverydayWidget.wallpaperHashcode)] =
        randomWallpaper.dynamicWallpapers.random().hashCode()
}