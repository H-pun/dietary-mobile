package dev.cisnux.dietary.domain

import android.net.Uri
import dev.cisnux.dietary.data.locals.FileService
import java.io.File
import javax.inject.Inject

class FileInteractor @Inject constructor(
    private val fileService: FileService
) : FileUseCase {
    override suspend fun createFile(): File = fileService.createFile()

    override suspend fun fileFromUri(image: Uri): File = fileService.fileFromUri(image = image)
}