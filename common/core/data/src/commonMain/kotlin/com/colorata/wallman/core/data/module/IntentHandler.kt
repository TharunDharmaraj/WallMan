package com.colorata.wallman.core.data.module

import com.colorata.wallman.core.data.Coordinates
import kotlin.reflect.KClass

interface IntentHandler {
    fun goToUrl(url: String)

    fun <T : Any> goToActivity(activity: KClass<T>)

    fun goToLiveWallpaper(packageName: String, serviceName: String)

    fun goToMaps(coordinates: Coordinates)

    fun goToPermissionPage(page: PermissionPage)

    fun exit()

}

enum class PermissionPage {
    InstallUnknownApps
}