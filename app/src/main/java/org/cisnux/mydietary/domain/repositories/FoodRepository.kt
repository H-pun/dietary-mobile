package org.cisnux.mydietary.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.cisnux.mydietary.domain.models.AddFoodDiary
import org.cisnux.mydietary.domain.models.FoodDiary
import org.cisnux.mydietary.domain.models.FoodDiaryDetail
import org.cisnux.mydietary.domain.models.FoodDiaryReport
import org.cisnux.mydietary.domain.models.FoodNutrition
import org.cisnux.mydietary.domain.models.Keyword
import org.cisnux.mydietary.commons.utils.FoodDiaryCategory
import org.cisnux.mydietary.commons.utils.ReportCategory
import org.cisnux.mydietary.commons.utils.UiState
import java.io.File

interface FoodRepository {
    val baseUrl: Flow<String>
    fun getFoodDiaries(
        accessToken: String,
        userId: String,
        date: String? = null,
        category: FoodDiaryCategory? = null,
        query: String? = null
    ): Flow<UiState<List<FoodDiary>>>

    fun getFoodDiaries(
        accessToken: String,
        userId: String,
        date: String,
    ): Flow<UiState<List<FoodDiary>>>

    fun getSuggestionKeywords(
        accessToken: String,
        userId: String,
        query: String,
    ): Flow<UiState<List<Keyword>>>

    fun addFoodDiary(
        accessToken: String,
        userId: String,
        addFoodDiary: AddFoodDiary
    ): Flow<UiState<String>>

    fun predictFoods(
        accessToken: String,
        foodPicture: File
    ): Flow<UiState<FoodNutrition>>

    fun deleteFoodDiaryById(accessToken: String, foodDiaryId: String): Flow<UiState<Nothing>>
    fun getFoodDiaryDetailById(
        accessToken: String,
        foodDiaryId: String
    ): Flow<UiState<FoodDiaryDetail>>

    fun getFoodDiaryReports(
        accessToken: String,
        userId: String,
        category: ReportCategory
    ): Flow<UiState<List<FoodDiaryReport>>>

    suspend fun updateBaseUrlApi(baseUrl: String)
}
