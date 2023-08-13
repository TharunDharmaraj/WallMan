package com.colorata.wallman.core.data.module

interface Logger {
    fun info(tag: String, message: String)

    fun warn(tag: String, message: String)

    fun error(tag: String, message: String)

    fun debug(tag: String, message: String)

    fun verbose(tag: String, message: String)

    suspend fun allLogs(): String
}

fun Logger.throwable(throwable: Throwable) {
    error("Error", throwable.stackTraceToString())
}