package com.colorata.wallman.core.data.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

fun <T, R> StateFlow<T>.mapState(scope: CoroutineScope, convert: (T) -> R): StateFlow<R> =
    map { convert(it) }.stateIn(scope, SharingStarted.Eagerly, convert(value))