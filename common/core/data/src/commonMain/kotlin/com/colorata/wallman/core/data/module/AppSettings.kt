package com.colorata.wallman.core.data.module

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val mirror: String = "https://sam.nl.tab.digital/s/wqZaeixFAsDEdGe/download?path=/",
    val mirrors: ImmutableList<String> =
        persistentListOf("https://sam.nl.tab.digital/s/wqZaeixFAsDEdGe/download?path=/"),
    val proceededGreeting: Boolean = false
)