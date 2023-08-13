package com.colorata.wallman.core.impl

import android.app.Activity
import android.app.Application
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import com.colorata.wallman.core.data.module.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CoreModuleImpl(private val application: Application) : CoreModule {
    override val coroutineScope: CoroutineScope by lazy { CoroutineScopeImpl(logger) }

    // Passing new coroutine scope
    // because passing existing coroutine scope
    // causes recursive problems when error is thrown
    override val logger: Logger by lazy { LoggerImpl(application, CoroutineScope(Dispatchers.IO)) }

    override var permissionHandler: PermissionHandler = PermissionHandler.NoopPermissionHandler
    override val intentHandler by lazy { IntentHandlerImpl(application) }
    override val systemProvider: SystemProvider by lazy {
        SystemProviderImpl(
            application,
            coroutineScope,
            logger
        )
    }
    override val appsProvider: AppsProvider by lazy { AppsProviderImpl(application) }
    override val downloadHandler: DownloadHandler by lazy {
        DownloadHandlerImpl(
            application,
            coroutineScope,
            logger
        )
    }
    override val fileHandler: FileHandler by lazy { FileHandlerImpl() }

    override val applicationSettings by lazy {
        ApplicationSettingsImpl(application, coroutineScope, logger)
    }
    override val navigationController: NavigationController by lazy {
        NavigationControllerImpl(logger)
    }

    override var windowProvider: WindowProvider = WindowProvider.NoopWindowProvider
}

fun CoreModuleImpl.applyActivity(activity: Activity) {
    permissionHandler = PermissionHandlerImpl(activity)
    intentHandler.setActivityContext(activity)
}

fun CoreModuleImpl.applyWindowSize(windowSize: WindowSizeClass) {
    windowProvider = WindowProviderImpl(windowSize)
}