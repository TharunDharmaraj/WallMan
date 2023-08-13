package com.colorata.wallman.core.data.module

import com.colorata.wallman.core.data.Result

interface FileHandler {
    suspend fun unzip(zipFilePath: String, destinationPath: String): Result<Unit>
}