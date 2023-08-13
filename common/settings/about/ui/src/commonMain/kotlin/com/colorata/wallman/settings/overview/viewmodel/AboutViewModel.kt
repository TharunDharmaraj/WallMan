package com.colorata.wallman.settings.overview.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.colorata.wallman.core.data.Polyglot
import com.colorata.wallman.core.data.Strings
import com.colorata.wallman.core.data.launchIO
import com.colorata.wallman.core.data.lazyMolecule
import com.colorata.wallman.core.data.module.CoreModule
import com.colorata.wallman.core.data.module.IntentHandler
import com.colorata.wallman.core.data.module.Logger
import com.colorata.wallman.core.data.module.SystemProvider
import com.colorata.wallman.core.data.module.throwable
import com.colorata.wallman.settings.about.ui.EasterActivity
import com.colorata.wallman.ui.icons.BugReport
import com.colorata.wallman.ui.icons.Code
import com.colorata.wallman.ui.icons.ContentCopy
import com.colorata.wallman.ui.icons.CurrencyRuble
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

fun CoreModule.AboutViewModel() = AboutViewModel(intentHandler, systemProvider, logger)

class AboutViewModel(
    private val intentHandler: IntentHandler,
    private val systemProvider: SystemProvider,
    private val logger: Logger
) : ViewModel() {

    data class AboutItem(
        val name: Polyglot,
        val description: Polyglot,
        val icon: ImageVector,
        val onClick: () -> Unit
    )

    private var clicks by mutableIntStateOf(0)
    private val items = persistentListOf(AboutItem(
        Strings.versionOfWallMan,
        Strings.actualVersion,
        Icons.Default.Info
    ) {
        onClickOnVersion()
    }, AboutItem(
        Strings.developer,
        Strings.colorata,
        Icons.Default.Person
    ) {
        intentHandler.goToUrl("https://gitlab.com/Colorata")
    }, AboutItem(
        Strings.groupInTelegram,
        Strings.tapToOpen,
        Icons.Default.Send
    ) {
        intentHandler.goToUrl("https://t.me/colorataNews")
    }, AboutItem(
        Strings.gitlab,
        Strings.tapToOpen,
        Icons.Default.Code
    ) {
        intentHandler.goToUrl("https://gitlab.com/Colorata/WallMan")
    }, AboutItem(
        Strings.supportWithQiwi,
        Strings.tapToOpen,
        Icons.Default.CurrencyRuble
    ) {
        intentHandler.goToUrl("https://qiwi.com/n/COLORATA")
    }, AboutItem(
        Strings.reportBug,
        Strings.requiresAccountInGitlab,
        Icons.Default.BugReport
    ) {
        intentHandler.goToUrl("https://gitlab.com/Colorata/WallMan/issues")
    },
        AboutItem(
            Strings.copyLogs,
            Strings.tapToCopy,
            Icons.Default.ContentCopy
        ) {
            viewModelScope.launchIO({ logger.throwable(it) }) {
                systemProvider.putToClipboard(Strings.logs.value, logger.allLogs())
            }
        }
    )


    private fun onClickOnVersion() {
        clicks += 1
        if (clicks == 10) {
            intentHandler.goToActivity(EasterActivity::class)
            clicks = 0
        }
    }

    val state by lazyMolecule {
        AboutScreenState(
            items,
            clicks
        ) { event ->
            when (event) {
                AboutScreenEvent.ClickOnVersion -> onClickOnVersion()
            }
        }
    }

    data class AboutScreenState(
        val aboutItems: ImmutableList<AboutItem>,
        val clicksOnVersion: Int,
        val onEvent: (AboutScreenEvent) -> Unit
    )

    @Immutable
    sealed interface AboutScreenEvent {
        data object ClickOnVersion : AboutScreenEvent
    }
}