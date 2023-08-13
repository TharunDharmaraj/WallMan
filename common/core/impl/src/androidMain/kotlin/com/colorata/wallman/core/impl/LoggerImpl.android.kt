package com.colorata.wallman.core.impl

import android.content.Context
import android.icu.util.Calendar
import android.util.Log
import com.colorata.wallman.core.data.AppConfiguration
import com.colorata.wallman.core.data.module.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

class LoggerImpl(
    private val context: Context,
    private val scope: CoroutineScope
) : Logger {
    override fun info(tag: String, message: String) {
        log(LogType.Info, tag, message)
    }

    override fun warn(tag: String, message: String) {
        log(LogType.Warn, tag, message)
    }

    override fun error(tag: String, message: String) {
        log(LogType.Error, tag, message)
    }

    override fun debug(tag: String, message: String) {
        log(LogType.Debug, tag, message)
    }

    override fun verbose(tag: String, message: String) {
        log(LogType.Verbose, tag, message)
    }

    private fun log(logType: LogType, tag: String, message: String) {
        when (logType) {
            LogType.Info -> Log.i(tag, message)
            LogType.Warn -> Log.w(tag, message)
            LogType.Error -> Log.e(tag, message)
            LogType.Debug -> Log.d(tag, message)
            LogType.Verbose -> Log.v(tag, message)
        }
        storeLog(logType, tag, message)
    }

    private val store by lazy {
        File(context.filesDir.absolutePath + "/" + "logs.txt").apply {
            if (!exists()) createNewFile()
        }
    }

    private fun storeLog(logType: LogType, tag: String, message: String) {
        scope.launch {
            val log = buildString {
                val versionName = AppConfiguration.VERSION_NAME
                val versionCode = AppConfiguration.VERSION_CODE
                appendLine("VERSION_CODE: $versionCode, VERSION_NAME: ${versionName}\n${getDate()}\n${logType.name}: $tag\n$message")
                appendLine()
            }
            store.appendText(log)
        }
    }

    private fun getDate(): String {
        return Calendar.getInstance().time.toString()
    }

    override suspend fun allLogs(): String {
        return store.readText()
    }

    private enum class LogType {
        Info,
        Warn,
        Error,
        Debug,
        Verbose
    }
}