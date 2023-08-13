package com.colorata.wallman.shared

import android.app.Application
import android.content.Context
import com.colorata.wallman.core.di.Graph
import com.colorata.wallman.core.di.impl.GraphImpl
import com.colorata.wallman.widget.impl.EverydayWidgetRepositoryImpl

class WallManApp : Application(), com.colorata.wallman.core.di.WallManApp {
    override val graph by lazy { GraphImpl(this) }
    override fun onCreate() {
        super.onCreate()
        graph.widgetModule.widgetRepository = EverydayWidgetRepositoryImpl(currentShapeId = null, this)
        graph.widgetModule.widgetRepository.initializeWorkManager()
        graph.coreModule.applicationSettings.settings()
    }
}

val Context.graph: Graph
    get() = (applicationContext as WallManApp).graph