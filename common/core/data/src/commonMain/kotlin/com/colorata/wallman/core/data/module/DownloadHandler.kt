package com.colorata.wallman.core.data.module

import com.colorata.wallman.core.data.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface DownloadHandler {
    suspend fun downloadFile(url: String, path: String, update: (Float) -> Unit = { }): Result<Unit>

    fun downloadFileInBackground(
        url: String,
        path: String,
        description: String
    ): Flow<Result<Unit>>

    fun stopDownloadingFileInBackground(url: String, path: String)

    object NoopDownloadHandler : DownloadHandler {
        override suspend fun downloadFile(
            url: String,
            path: String,
            update: (Float) -> Unit
        ): Result<Unit> {
            return Result.Error(Throwable("DownloadHandler not provided"))
        }

        override fun downloadFileInBackground(
            url: String,
            path: String,
            description: String
        ): Flow<Result<Unit>> {
            return flow { }
        }

        override fun stopDownloadingFileInBackground(url: String, path: String) {}
    }
}