package dev.cisnux.dietary.domain.usecases

import android.net.Uri
import dev.cisnux.dietary.domain.repositories.FileRepository
import java.io.File
import javax.inject.Inject

class FileInteractor @Inject constructor(
    private val fileRepository: FileRepository
) : FileUseCase {
    override suspend fun createFile(): File = fileRepository.createFile()

    override suspend fun fileFromUri(image: Uri): File = fileRepository.fileFromUri(image = image)
}