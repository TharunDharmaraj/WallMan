package com.colorata.wallman.core.data.module

interface PermissionHandler {
    fun requestPermissions(permissions: List<String>, requestCode: Int = 0)

    fun isPermissionGranted(permission: Permission): Boolean

    object NoopPermissionHandler : PermissionHandler {
        override fun requestPermissions(permissions: List<String>, requestCode: Int) {}
        override fun isPermissionGranted(permission: Permission): Boolean = false
    }
}

enum class Permission {
    InstallUnknownApps
}