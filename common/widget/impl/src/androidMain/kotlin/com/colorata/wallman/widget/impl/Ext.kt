package com.colorata.wallman.widget.impl

import android.app.Activity
import android.appwidget.AppWidgetManager
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.colorata.wallman.widget.api.EverydayWidget
import com.colorata.wallman.widget.ui_widget.EverydayWidgetContent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

fun Activity.isWidgetConfiguration(): Boolean {
    return currentAppWidgetId() != AppWidgetManager.INVALID_APPWIDGET_ID
}

fun Activity.currentAppWidgetId(): Int = intent?.extras?.getInt(
    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
) ?: AppWidgetManager.INVALID_APPWIDGET_ID

fun Activity.currentWidgetShapeId(): Flow<Int?> {
    return channelFlow {
        send(null)
        val manager = GlanceAppWidgetManager(this@currentWidgetShapeId)
        val glanceIds = manager.getGlanceIdBy(currentAppWidgetId())
        updateAppWidgetState(this@currentWidgetShapeId, glanceIds) {
            it.apply {
                val id =
                    this[intPreferencesKey(EverydayWidget.shapeKey)]
                        ?: com.colorata.wallman.widget.api.R.drawable.ic_clever
                send(id)
            }
        }
    }
}