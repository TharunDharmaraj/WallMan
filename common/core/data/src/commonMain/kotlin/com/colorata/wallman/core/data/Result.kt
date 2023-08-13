package com.colorata.wallman.core.data

sealed interface Result<T> {
    @JvmInline
    value class Error<T>(val throwable: Throwable) : Result<T>

    @JvmInline
    value class Loading<T>(val progress: Float) : Result<T>

    @JvmInline
    value class Success<T>(val result: T) : Result<T>
}

inline fun <T, R> Result<T>.map(block: (T) -> R): Result<R> {
    return when (this) {
        is Result.Error -> Result.Error(throwable)
        is Result.Loading -> Result.Loading(progress)
        is Result.Success -> Result.Success(block(result))
    }
}

inline fun <T> Result<T>.onError(block: (throwable: Throwable) -> Unit): Result<T> {
    if (this is Result.Error) block(throwable)
    return this
}

inline fun <T> Result<T>.onSuccess(block: (result: T) -> Unit): Result<T> {
    if (this is Result.Success) block(result)
    return this
}

inline fun <T> Result<T>.onLoading(block: (progress: Float) -> Unit): Result<T> {
    if (this is Result.Loading) block(progress)
    return this
}

inline fun <T> Result<T>.getOrElse(block: () -> T): T {
    return when (this) {
        is Result.Success -> result
        else -> block()
    }
}

fun <T> Result<T>.getOrDefault(default: T): T {
    return when (this) {
        is Result.Success -> result
        else -> default
    }
}

inline fun <T> runResulting(block: () -> T): Result<T> {
    return runCatching {
        return@runCatching Result.Success(block())
        }.getOrElse { Result.Error(it) }.onError { it.printStackTrace() }
}