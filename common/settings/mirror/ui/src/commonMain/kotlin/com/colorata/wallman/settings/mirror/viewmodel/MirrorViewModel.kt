package com.colorata.wallman.settings.mirror.viewmodel

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import com.colorata.wallman.core.data.module.ApplicationSettings
import com.colorata.wallman.core.data.module.CoreModule
import com.colorata.wallman.core.data.Strings
import com.colorata.wallman.core.data.lazyMolecule
import com.colorata.wallman.settings.mirror.api.Mirror
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.map

fun CoreModule.MirrorViewModel() = MirrorViewModel(applicationSettings)
class MirrorViewModel(
    private val applicationSettings: ApplicationSettings
) : ViewModel() {
    private val allMirrors: ImmutableList<Mirror> = persistentListOf(
        Mirror(
            Strings.original, "https://sam.nl.tab.digital/s/wqZaeixFAsDEdGe/download?path=/"
        ), Mirror(
            Strings.mirror1,
            "https://shared02.opsone-cloud.ch/index.php/s/CJW7DGrKskNJMm9/download?path=/"
        )
    )
    private val settings = applicationSettings.settings()
    private val defaultMirror = allMirrors[0]
    private val selectedMirror = settings.map { settings -> allMirrors.first { it.url == settings.mirror } }

    private fun selectMirror(mirror: Mirror) {
        applicationSettings.mutate { it.copy(mirror = mirror.url) }
    }
    val state by lazyMolecule {
        val selectedMirror by selectedMirror.collectAsState(initial = defaultMirror)
        val mirrors = allMirrors
        return@lazyMolecule MirrorScreenState(mirrors, selectedMirror) { event ->
            when (event) {
                is MirrorScreenEvent.SelectMirror -> selectMirror(event.mirror)
            }
        }
    }

    data class MirrorScreenState(
        val mirrors: ImmutableList<Mirror>,
        val selectedMirror: Mirror,
        val onEvent: (MirrorScreenEvent) -> Unit
    )

    @Immutable
    sealed interface MirrorScreenEvent {
        data class SelectMirror(val mirror: Mirror) : MirrorScreenEvent
    }
}