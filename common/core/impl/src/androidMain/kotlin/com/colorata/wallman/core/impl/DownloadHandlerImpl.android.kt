package com.colorata.wallman.core.impl

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import com.colorata.wallman.core.data.module.DownloadHandler
import com.colorata.wallman.core.data.Strings
import com.colorata.wallman.core.data.Result
import com.colorata.wallman.core.data.launchIO
import com.colorata.wallman.core.data.module.Logger
import com.colorata.wallman.core.data.module.throwable
import com.colorata.wallman.core.data.withIO
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File

class DownloadHandlerImpl(
    private val context: Context,
    private val scope: CoroutineScope,
    private val logger: Logger
) : DownloadHandler {
    private val mutex = Mutex()
    private val downloadIdsByUrl = mutableMapOf<String, Long>()
    private val downloadManager by lazy { context.getSystemService(DownloadManager::class.java) }
    private val client by lazy { HttpClient() }
    override suspend fun downloadFile(
        url: String,
        path: String,
        update: (Float) -> Unit
    ): Result<Unit> {
        return runCatching {
            client.downloadLarge(url, path, update)
            Result.Success(Unit)
        }.onFailure {
            File(path).delete()
        }.getOrElse { Result.Error(it) }
    }

    @SuppressLint("Range")
    override fun downloadFileInBackground(
        url: String,
        path: String,
        description: String
    ): Flow<Result<Unit>> {
        return flow {
            emit(Result.Loading(0f))
            val downloadFile = File(path)
            withIO {
                if (downloadFile.exists()) downloadFile.delete()
            }

            val request = createDownloadRequest(url, downloadFile, description)
            val id = downloadManager.enqueue(request)
            mutex.withLock {
                downloadIdsByUrl[url] = id
            }
            while (true) {
                watchForDownloadProgress(id, onUpdate = {
                    emit(Result.Loading(it))
                }, onSuccess = {
                    emit(Result.Success(Unit))
                    mutex.withLock {
                        downloadIdsByUrl.remove(url)
                    }
                    return@flow
                }, onError = {
                    emit(Result.Error(Throwable("Cannot download $description")))
                    downloadFile.delete()
                    mutex.withLock {
                        downloadIdsByUrl.remove(url)
                    }
                    return@flow
                })
                mutex.withLock {
                    downloadIdsByUrl.remove(url)
                }
            }
        }
    }

    override fun stopDownloadingFileInBackground(url: String, path: String) {
        scope.launchIO({ logger.throwable(it) }) {
            mutex.withLock {
                if (downloadIdsByUrl.containsKey(url)) {
                    downloadManager.remove(downloadIdsByUrl[url] ?: 0)
                    downloadIdsByUrl.remove(url)
                }
            }
        }
    }

    @SuppressLint("Range")
    private inline fun watchForDownloadProgress(
        id: Long,
        onUpdate: (Float) -> Unit,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        while (true) {
            val query = DownloadManager.Query().apply { setFilterById(id) }
            val cursor = downloadManager.query(query).apply { moveToFirst() }
            runCatching {
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    onSuccess()
                } else if (status == DownloadManager.STATUS_FAILED) {
                    onError()
                }
                val bytesDownloaded =
                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                val bytesTotal =
                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                cursor.close()
                val progress = bytesDownloaded.toFloat() / bytesTotal.toFloat()
                if (progress == 1f) {
                    onSuccess()
                }
                onUpdate(progress)
            }.onFailure {
                onError()
            }
        }
    }

    private fun createDownloadRequest(url: String, downloadFile: File, description: String) =
        DownloadManager.Request(Uri.parse(url)).apply {
            setDescription(Strings.downloading.value)
            setTitle(description)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationUri(Uri.fromFile(downloadFile))
            setMimeType("application/vnd.android.package-archive")
        }
}

private suspend fun HttpClient.downloadLarge(
    url: String,
    path: String,
    update: (Float) -> Unit = {}
) {
    val file = File(path)
    if (file.exists()) file.delete()
    file.parentFile?.mkdirs()
    file.createNewFile()
    prepareGet(url).execute { response ->
        var pastPercent = 0
        val channel: ByteReadChannel = response.body()
        val totalLength = response.contentLength()?.toFloat() ?: 100f
        while (!channel.isClosedForRead) {
            val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
            while (packet.isNotEmpty) {
                val bytes = packet.readBytes()
                file.appendBytes(bytes)
                val fileLength = file.length().toFloat()
                val currentPercent = fileLength / totalLength
                if ((currentPercent * 1000).toInt() != pastPercent) {
                    update(fileLength / totalLength)
                    pastPercent = (currentPercent * 1000).toInt()
                }
            }
        }
    }

    // TODO: Replace with normal implementation to trigger FileObserver
    val tempFile = File("$path.temp")
    tempFile.createNewFile()
    tempFile.delete()
}