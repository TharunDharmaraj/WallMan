package com.colorata.wallman.shared

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Info
import androidx.compose.ui.graphics.vector.ImageVector
import com.colorata.wallman.categories.api.CategoriesDestination
import com.colorata.wallman.core.data.Destination
import com.colorata.wallman.core.data.Destinations
import com.colorata.wallman.core.data.Polyglot
import com.colorata.wallman.core.data.Strings
import com.colorata.wallman.settings.overview.api.SettingsOverviewDestination
import com.colorata.wallman.ui.icons.AutoAwesome
import com.colorata.wallman.ui.icons.Folder
import com.colorata.wallman.wallpapers.MainDestination
import kotlinx.collections.immutable.persistentListOf

data class QuickNavigationDestination(
    val destination: Destination,
    val previewName: Polyglot,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector = filledIcon,
)

internal val quickAccessibleDestinations = persistentListOf(
    QuickNavigationDestination(
        destination = Destinations.MainDestination(),
        filledIcon = Icons.Filled.AutoAwesome,
        previewName = Strings.explore
    ),
    QuickNavigationDestination(
        destination = Destinations.CategoriesDestination(),
        filledIcon = Icons.Filled.Folder,
        outlinedIcon = Icons.Outlined.Folder,
        previewName = Strings.categories
    ),
    QuickNavigationDestination(
        destination = Destinations.SettingsOverviewDestination(),
        filledIcon = Icons.Filled.Info,
        outlinedIcon = Icons.Outlined.Info,
        previewName = Strings.more
    ),
)