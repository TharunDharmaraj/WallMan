package com.colorata.wallman.wallpapers

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

data class AndroidVersionCompatibilityChecker(internal val minSdk: Int): CompatibilityChecker {
    @ChecksSdkIntAtLeast(parameter = 0)
    override fun isCompatible(): Boolean {
        return Build.VERSION.SDK_INT >= minSdk
    }
}
