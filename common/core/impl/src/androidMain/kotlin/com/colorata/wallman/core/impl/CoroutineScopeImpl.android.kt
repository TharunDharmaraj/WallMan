package com.colorata.wallman.core.impl

import com.colorata.wallman.core.data.module.Logger
import com.colorata.wallman.core.data.module.throwable
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

internal fun CoroutineScopeImpl(logger: Logger): CoroutineScope {
    return CoroutineScope(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
        logger.throwable(throwable)
    })
}