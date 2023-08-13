package com.colorata.wallman.core.data

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.colorata.wallman.core.data.molecule.RecompositionMode
import com.colorata.wallman.core.data.molecule.launchMolecule
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

inline fun CoroutineScope.launchWithExceptionHandling(
    crossinline onError: suspend (Throwable) -> Unit,
    crossinline block: suspend CoroutineScope.() -> Unit,
    dispatcher: CoroutineContext,
    exceptionDispatcher: CoroutineContext,
): Job {
    val handler = CoroutineExceptionHandler { _, throwable ->
        launch(exceptionDispatcher) {
            onError(throwable)
        }
    }
    return launch(dispatcher + handler) { block() }
}

inline fun CoroutineScope.launchIO(
    crossinline onError: suspend (Throwable) -> Unit,
    crossinline block: suspend CoroutineScope.() -> Unit
): Job {
    return launchWithExceptionHandling(
        onError, block, dispatcher = Dispatchers.IO, exceptionDispatcher = Dispatchers.Main
    )
}

inline fun CoroutineScope.launchMain(
    crossinline onError: suspend (Throwable) -> Unit,
    crossinline block: suspend CoroutineScope.() -> Unit
): Job {
    return launchWithExceptionHandling(
        onError, block, dispatcher = Dispatchers.Main, exceptionDispatcher = Dispatchers.Main
    )
}

suspend inline fun <T> withIO(crossinline block: suspend () -> T) {
    withContext(Dispatchers.IO) { block() }
}

suspend inline fun <T> withMain(crossinline block: suspend () -> T) {
    withContext(Dispatchers.Main) { block() }
}

fun <T> ViewModel.lazyMolecule(content: @Composable () -> T) =
    lazy { viewModelScope.launchMolecule(RecompositionMode.Immediate, content) }

fun <T> CoroutineScope.launchMolecule(content: @Composable () -> T) =
    launchMolecule(RecompositionMode.Immediate, content)
