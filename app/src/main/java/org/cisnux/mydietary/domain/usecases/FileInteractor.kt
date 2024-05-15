package org.cisnux.mydietary.domain.usecases

import android.net.Uri
import org.cisnux.mydietary.domain.repositories.FileRepository
import java.io.File
import javax.inject.Inject

class FileInteractor @Inject constructor(
    private val fileRepository: FileRepository
) : FileUseCase {
    override suspend fun createFile(): File = fileRepository.createFile()

    override suspend fun fileFromUri(image: Uri): File = fileRepository.fileFromUri(image = image)
}