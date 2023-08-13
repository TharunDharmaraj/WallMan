package com.colorata.wallman.core.data

import androidx.compose.runtime.Immutable

@Immutable
sealed interface DownloadState {
    data class Error(val throwable: Throwable) : DownloadState

    data object NotConnected : DownloadState

    data object Connecting : DownloadState

    data class Downloading(val progress: Float) : DownloadState

    data object Downloaded : DownloadState
}

fun DownloadState.displayedText(): Polyglot = when (this) {
    DownloadState.Connecting -> Strings.connecting
    DownloadState.Downloaded -> Strings.downloaded
    is DownloadState.Downloading -> Strings.downloading
    is DownloadState.Error -> Strings.error
    DownloadState.NotConnected -> Strings.notConnected
}