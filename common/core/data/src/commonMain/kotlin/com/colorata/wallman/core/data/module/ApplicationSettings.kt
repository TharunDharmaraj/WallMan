package com.colorata.wallman.core.data.module

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface ApplicationSettings {
    fun settings(): StateFlow<AppSettings>

    fun nullableSettings(): StateFlow<AppSettings?>

    fun mutate(block: (AppSettings) -> AppSettings)

    object NoopApplicationSettings : ApplicationSettings {
        override fun settings(): StateFlow<AppSettings> {
            return MutableStateFlow(AppSettings())
        }

        override fun nullableSettings(): StateFlow<AppSettings?> {
            return MutableStateFlow(null)
        }

        override fun mutate(block: (AppSettings) -> AppSettings) {

        }
    }
}