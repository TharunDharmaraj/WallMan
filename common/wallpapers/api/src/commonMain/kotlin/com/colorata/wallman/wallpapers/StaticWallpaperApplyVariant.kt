package com.colorata.wallman.wallpapers

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.ui.graphics.vector.ImageVector
import com.colorata.wallman.core.data.Polyglot
import com.colorata.wallman.core.data.Strings

enum class StaticWallpaperApplyVariant(
    val previewName: Polyglot,
    val icon: ImageVector,
    val secondIcon: ImageVector? = null
) {
    LockScreen(Strings.lockScreen, Icons.Default.Lock),
    HomeScreen(Strings.homeScreen, Icons.Default.Home),
    Both(Strings.both, Icons.Default.Lock, Icons.Default.Home)
}