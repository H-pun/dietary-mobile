package org.cisnux.mydietary.domain.repositories

import android.net.Uri
import java.io.File

interface FileRepository {
    suspend fun createFile(): File
    suspend fun fileFromUri(image: Uri): File
}