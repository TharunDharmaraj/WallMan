package com.colorata.wallman.core.impl

import android.app.Activity
import android.content.pm.PackageManager
import com.colorata.wallman.core.data.module.Permission
import com.colorata.wallman.core.data.module.PermissionHandler

class PermissionHandlerImpl(private val activity: Activity) : PermissionHandler {
    override fun requestPermissions(permissions: List<String>, requestCode: Int) {
        activity.requestPermissions(permissions.toTypedArray(), requestCode)
    }

    override fun isPermissionGranted(permission: Permission): Boolean {
        return permission.isGranted()
    }

    private fun Permission.isGranted() = when (this) {
        Permission.InstallUnknownApps -> activity.packageManager.canRequestPackageInstalls()
    }
}