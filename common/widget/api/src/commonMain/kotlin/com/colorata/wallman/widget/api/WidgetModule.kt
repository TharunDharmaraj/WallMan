package com.colorata.wallman.widget.api

import com.colorata.wallman.core.data.module.CoreModule

interface WidgetModule : CoreModule {
    val widgetRepository: EverydayWidgetRepository
}
