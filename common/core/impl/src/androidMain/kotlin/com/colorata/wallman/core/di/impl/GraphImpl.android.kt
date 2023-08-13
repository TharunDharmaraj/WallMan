package com.colorata.wallman.core.di.impl

import android.app.Application
import androidx.activity.ComponentActivity
import com.colorata.wallman.core.di.Graph
import com.colorata.wallman.core.impl.CoreModuleImpl
import com.colorata.wallman.core.impl.applyActivity
import com.colorata.wallman.wallpapers.WallpapersModule
import com.colorata.wallman.wallpapers.impl.WallpapersModuleImpl
import com.colorata.wallman.widget.impl.WidgetModuleImpl
import com.colorata.wallman.widget.impl.applyActivity

actual class GraphImpl(private val application: Application) : Graph {
    override val coreModule by lazy { CoreModuleImpl(application) }

    override val wallpapersModule: WallpapersModule by lazy {
        WallpapersModuleImpl(coreModule, application)
    }

    override val widgetModule by lazy { WidgetModuleImpl(coreModule) }
}

fun GraphImpl.applyActivity(activity: ComponentActivity) {
    coreModule.applyActivity(activity)
    widgetModule.applyActivity(activity)
}