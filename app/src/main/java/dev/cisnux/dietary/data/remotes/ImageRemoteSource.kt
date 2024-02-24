package dev.cisnux.dietary.data.remotes

import java.io.File

interface ImageRemoteSource {
    suspend fun addFoodDiaryImage(foodDiaryId: String, file: File)
    suspend fun getFoodDiaryImageById(foodDiaryId: String): String?
    suspend fun deleteFoodDiaryById(foodDiaryId: String)
}