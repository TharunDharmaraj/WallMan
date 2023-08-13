package com.colorata.wallman.core.data.module

import com.colorata.wallman.core.data.Loadable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface SystemProvider: Loadable {
    fun filesInDirectory(path: String): StateFlow<List<String>>

    fun putToClipboard(label: String, text: String)

    val cacheDirectoryPath: String

    val filesDirectoryPath: String

    val externalCacheDirectoryPath: String

    val externalFilesDirectoryPath: String

}