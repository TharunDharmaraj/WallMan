package com.colorata.wallman.core.impl

import android.app.Activity
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import com.colorata.wallman.core.data.Coordinates
import com.colorata.wallman.core.data.Strings
import com.colorata.wallman.core.data.module.IntentHandler
import com.colorata.wallman.core.data.module.PermissionPage
import com.colorata.wallman.core.data.onError
import com.colorata.wallman.core.data.runResulting
import kotlin.reflect.KClass

class IntentHandlerImpl(private var context: Context) : IntentHandler {

    fun setActivityContext(context: Context) {
        this.context = context
    }

    override fun goToUrl(url: String) {
        runResulting {
            Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }.start()
        }.onError {
            showToast(Strings.browserNotInstalled.value)
        }
    }

    override fun <T : Any> goToActivity(activity: KClass<T>) {
        Intent(context, activity.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }.start()
    }

    override fun goToLiveWallpaper(packageName: String, serviceName: String) {
        runResulting {
            val component = ComponentName(
                packageName,
                serviceName
            )
            Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
                .apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, component)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
                .start()
        }.onError {
            showToast(Strings.wallpaperNotFound.value)
        }
    }

    override fun goToMaps(coordinates: Coordinates) {
        runResulting {
            val url =
                when (coordinates) {
                    is Coordinates.ExactCoordinates ->
                        "geo:${coordinates.latitude},${coordinates.longitude}?z=16"

                    is Coordinates.AddressCoordinates ->
                        "geo:0,0?q=${coordinates.address.replace(" ", "+")}"
                }
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(url)
            ).start()
        }.onError {
            showToast(Strings.mapsAppNotInstalled.value)
        }
    }

    override fun exit() {
        (context as Activity).finish()
    }

    override fun goToPermissionPage(page: PermissionPage) {
        page.toIntent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).start()
    }

    private fun PermissionPage.toIntent() = when (this) {
        PermissionPage.InstallUnknownApps -> Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(
            Uri.parse("package:${context.packageName}")
        )
    }

    private fun Intent.start() = context.startActivity(this)

    private fun showToast(message: String) =
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}