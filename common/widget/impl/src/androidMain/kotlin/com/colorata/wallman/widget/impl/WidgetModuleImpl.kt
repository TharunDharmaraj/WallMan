package com.colorata.wallman.widget.impl

import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.colorata.wallman.core.data.module.CoreModule
import com.colorata.wallman.core.data.launchIO
import com.colorata.wallman.core.data.module.throwable
import com.colorata.wallman.widget.api.EverydayWidgetRepository
import com.colorata.wallman.widget.api.WidgetModule

class WidgetModuleImpl(coreModule: CoreModule) : WidgetModule, CoreModule by coreModule {
    override var widgetRepository: EverydayWidgetRepository = EverydayWidgetRepository.NoopEverydayWidgetRepository
}

fun WidgetModuleImpl.applyActivity(activity: ComponentActivity) {
    val isShapeConfiguration = activity.isWidgetConfiguration()
    if (isShapeConfiguration) activity.lifecycleScope.launchIO({ logger.throwable(it) }) {
        activity.currentWidgetShapeId().collect {
            widgetRepository = EverydayWidgetRepositoryImpl(it, activity).apply { setActivity(activity) }
        }
    }
}