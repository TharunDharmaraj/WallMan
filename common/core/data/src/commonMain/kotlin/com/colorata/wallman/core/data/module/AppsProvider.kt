package com.colorata.wallman.core.data.module

import com.colorata.wallman.core.data.Loadable
import com.colorata.wallman.core.data.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface AppsProvider: Loadable {
    fun installedApps(): StateFlow<List<String>>

    fun installApp(path: String): Result<Unit>

    fun deleteApp(packageName: String): Result<Unit>

    fun update()
}