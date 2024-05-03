package dev.cisnux.dietary.domain.usecases

import dev.cisnux.dietary.domain.models.AddFoodDiary
import dev.cisnux.dietary.domain.models.FoodDiary
import dev.cisnux.dietary.domain.models.FoodDiaryQuestion
import dev.cisnux.dietary.domain.models.FoodDiaryDetail
import dev.cisnux.dietary.domain.models.FoodNutrition
import dev.cisnux.dietary.domain.models.UserNutrition
import dev.cisnux.dietary.domain.models.Report
import dev.cisnux.dietary.utils.FoodDiaryCategory
import dev.cisnux.dietary.utils.ReportCategory
import dev.cisnux.dietary.utils.UiState
import kotlinx.coroutines.flow.Flow
import java.io.File

interface FoodDiaryUseCase {
    fun getDiaryFoodsByDays(date: String, category: FoodDiaryCategory): Flow<UiState<List<FoodDiary>>>
    fun getDiaryFoodsByQuery(query: String): Flow<UiState<List<FoodDiary>>>
    fun getKeywordSuggestionsByQuery(query: String): Flow<UiState<List<String>>>
    fun addFoodDiary(addFoodDiary: AddFoodDiary): Flow<UiState<String>>
    fun getFoodDiaryDetailById(foodDiaryId: String): Flow<UiState<FoodDiaryDetail>>

    fun deleteFoodDiaryById(foodDiaryId: String): Flow<UiState<Nothing>>

    fun getFoodDiaryReports(category: ReportCategory): Flow<UiState<Report>>
    fun predictFoods(foodPicture: File): Flow<UiState<Pair<UserNutrition, FoodNutrition>>>

    val baseUrl: Flow<String>
    suspend fun updateBaseUrlApi(baseUrl: String)
}