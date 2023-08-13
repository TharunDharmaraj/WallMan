package com.colorata.wallman.settings.overview.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.colorata.wallman.core.data.*
import com.colorata.wallman.core.data.module.CoreModule
import com.colorata.wallman.core.data.module.NavigationController
import com.colorata.wallman.settings.about.api.AboutDestination
import com.colorata.wallman.settings.memory.api.MemoryDestination
import com.colorata.wallman.settings.mirror.api.MirrorDestination
import com.colorata.wallman.ui.icons.ContentCopy
import com.colorata.wallman.ui.icons.Storage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

fun CoreModule.SettingsViewModel() = SettingsViewModel(navigationController)

class SettingsViewModel(
    private val navigation: NavigationController
) : ViewModel() {
    private val items = persistentListOf(
        SettingsItem(
            Strings.memoryOptimization,
            Strings.keepYourMemoryFree,
            Icons.Default.Storage,
            Destinations.MemoryDestination()
        ),
        SettingsItem(
            Strings.mirrors,
            Strings.youCanAddOtherMirrorIfCurrentDoesNotWork,
            Icons.Default.ContentCopy,
            Destinations.MirrorDestination()
        ),
        SettingsItem(
            Strings.aboutWallMan,
            Strings.contactInfoDevelopersMore,
            Icons.Default.Info,
            Destinations.AboutDestination()
        )
    )

    data class SettingsItem(
        val name: Polyglot,
        val description: Polyglot,
        val icon: ImageVector,
        val destination: Destination
    )


    private fun onSettingsClick(
        setting: SettingsItem
    ) {
        navigation.navigate(setting.destination)
    }

    val state by lazyMolecule {
        SettingsScreenState(
            items
        ) { event ->
            when (event) {
                is SettingsScreenEvent.GoToSettings -> onSettingsClick(event.setting)
            }
        }
    }

    data class SettingsScreenState(
        val settingsItems: ImmutableList<SettingsItem>,
        val onEvent: (SettingsScreenEvent) -> Unit
    )

    @Immutable
    sealed interface SettingsScreenEvent {
        data class GoToSettings(val setting: SettingsItem) : SettingsScreenEvent
    }
}