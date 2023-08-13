package com.colorata.wallman.core.impl

import com.colorata.wallman.core.data.Result
import com.colorata.wallman.core.data.module.FileHandler
import com.colorata.wallman.core.data.runResulting
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipFile

internal class FileHandlerImpl : FileHandler {
    override suspend fun unzip(zipFilePath: String, destinationPath: String): Result<Unit> {
        return runResulting {
            File(destinationPath).run {
                if (!exists()) {
                    mkdirs()
                }
            }

            ZipFile(File(zipFilePath)).use { zip ->

                zip.entries().asSequence().forEach { entry ->

                    zip.getInputStream(entry).use { input ->


                        val filePath = destinationPath + File.separator + entry.name

                        if (!entry.isDirectory) {
                            extractFile(input, filePath)
                        } else {
                            val dir = File(filePath)
                            dir.mkdir()
                        }

                    }

                }
            }
        }
    }

    private fun extractFile(inputStream: InputStream, destFilePath: String) {
        val bos = BufferedOutputStream(FileOutputStream(destFilePath))
        val bytesIn = ByteArray(BUFFER_SIZE)
        var read: Int
        while (inputStream.read(bytesIn).also { read = it } != -1) {
            bos.write(bytesIn, 0, read)
        }
        bos.close()
    }

}


private const val BUFFER_SIZE = 4096
