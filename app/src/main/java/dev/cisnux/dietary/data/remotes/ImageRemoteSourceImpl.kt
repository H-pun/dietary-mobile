package dev.cisnux.dietary.data.remotes

import com.google.firebase.storage.FirebaseStorage
import dev.cisnux.dietary.utils.PATH
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject

class ImageRemoteSourceImpl @Inject constructor(
    private val storage: FirebaseStorage,
) : ImageRemoteSource {
    override suspend fun addFoodDiaryImage(foodDiaryId: String, file: File): Unit =
        withContext(Dispatchers.IO) {
            val storageRef = storage.reference.child("$PATH/$foodDiaryId.jpg")
            storageRef.putStream(FileInputStream(file)).await()
        }

    override suspend fun getFoodDiaryImageById(foodDiaryId: String): String =
        withContext(Dispatchers.IO) {
            val storageRef = storage.reference.child("$PATH/$foodDiaryId.jpg")
            val url = storageRef.downloadUrl.await()
            url.toString()
        }

    override suspend fun deleteFoodDiaryById(foodDiaryId: String): Unit =
        withContext(Dispatchers.IO) {
            val storageRef = storage.reference.child("$PATH/$foodDiaryId.jpg")
            storageRef.delete().await()
        }
}