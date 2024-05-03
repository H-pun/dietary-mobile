package dev.cisnux.dietary.domain.repositories

import dev.cisnux.dietary.domain.models.AddFoodDiary
import dev.cisnux.dietary.domain.models.FoodDiary
import dev.cisnux.dietary.domain.models.FoodDiaryDetail
import dev.cisnux.dietary.domain.models.FoodNutrition
import dev.cisnux.dietary.domain.models.UserNutrition
import dev.cisnux.dietary.domain.models.Report
import dev.cisnux.dietary.utils.FoodDiaryCategory
import dev.cisnux.dietary.utils.ReportCategory
import dev.cisnux.dietary.utils.UiState
import kotlinx.coroutines.flow.Flow
import java.io.File

interface FoodRepository {
    val baseUrl: Flow<String>
    fun getDiaryFoodsByDate(
        accessToken: String,
        userId: String,
        date: String,
        category: FoodDiaryCategory
    ): Flow<UiState<List<FoodDiary>>>

    fun getDiaryFoodsByQuery(query: String): Flow<UiState<List<FoodDiary>>>
    fun getKeywordSuggestionsByQuery(query: String): Flow<UiState<List<String>>>
    fun addFoodDiary(
        accessToken: String,
        userId: String,
        addFoodDiary: AddFoodDiary
    ): Flow<UiState<String>>

    fun predictFood(
        userId: String,
        accessToken: String,
        foodPicture: File,
        date: String
    ): Flow<UiState<Pair<UserNutrition, FoodNutrition>>>

    fun deleteFoodDiaryById(accessToken: String, foodDiaryId: String): Flow<UiState<Nothing>>
    fun getFoodDiaryDetailById(
        accessToken: String,
        foodDiaryId: String
    ): Flow<UiState<FoodDiaryDetail>>

    fun getFoodDiaryReports(category: ReportCategory): Flow<UiState<Report>>
    suspend fun updateBaseUrlApi(baseUrl: String)
}
