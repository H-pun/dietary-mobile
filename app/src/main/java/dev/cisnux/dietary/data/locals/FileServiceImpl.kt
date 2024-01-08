package dev.cisnux.dietary.data.locals

import android.content.Context
import android.net.Uri
import android.os.Environment
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject

class FileServiceImpl @Inject constructor(
    @ApplicationContext private val application: Context,
) : FileService {
    override suspend fun createFile(): File = withContext(Dispatchers.IO) {
        val storageDir: File? = application.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        File.createTempFile(System.currentTimeMillis().toString(), ".jpg", storageDir)
    }

    override suspend fun fileFromUri(image: Uri): File = withContext(Dispatchers.IO) {
        val contentResolver = application.contentResolver
        val storageDir: File? = application.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile(System.currentTimeMillis().toString(), ".jpg", storageDir)

        val inputStream = contentResolver.openInputStream(image) as InputStream
        val outputStream = FileOutputStream(file)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also {
                len = it
            } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        file
    }
}