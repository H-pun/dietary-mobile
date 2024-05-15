package org.cisnux.mydietary.domain.usecases

import android.net.Uri
import java.io.File

interface FileUseCase {
    suspend fun createFile(): File
    suspend fun fileFromUri(image: Uri): File
}