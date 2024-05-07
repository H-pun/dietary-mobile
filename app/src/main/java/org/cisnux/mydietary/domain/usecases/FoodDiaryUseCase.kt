package org.cisnux.mydietary.domain.usecases

import org.cisnux.mydietary.domain.models.AddFoodDiary
import org.cisnux.mydietary.domain.models.FoodDiary
import org.cisnux.mydietary.domain.models.FoodDiaryDetail
import org.cisnux.mydietary.domain.models.FoodNutrition
import org.cisnux.mydietary.domain.models.UserNutrition
import org.cisnux.mydietary.domain.models.Report
import org.cisnux.mydietary.utils.FoodDiaryCategory
import org.cisnux.mydietary.utils.ReportCategory
import org.cisnux.mydietary.utils.UiState
import kotlinx.coroutines.flow.Flow
import org.cisnux.mydietary.domain.models.FoodDiaryReport
import org.cisnux.mydietary.domain.models.Keyword
import java.io.File

interface FoodDiaryUseCase {
    fun getDiaryFoodsByDays(date: String, category: FoodDiaryCategory): Flow<UiState<List<FoodDiary>>>
    fun getDiaryFoodsByQuery(query: String): Flow<UiState<List<FoodDiary>>>
    fun getKeywordSuggestionsByQuery(query: String): Flow<UiState<List<Keyword>>>
    fun addFoodDiary(addFoodDiary: AddFoodDiary): Flow<UiState<String>>
    fun getFoodDiaryDetailById(foodDiaryId: String): Flow<UiState<FoodDiaryDetail>>

    fun deleteFoodDiaryById(foodDiaryId: String): Flow<UiState<Nothing>>

    fun getFoodDiaryReports(category: ReportCategory): Flow<UiState<List<FoodDiaryReport>>>
    fun predictFoods(foodPicture: File): Flow<UiState<Pair<UserNutrition, FoodNutrition>>>

    val baseUrl: Flow<String>
    suspend fun updateBaseUrlApi(baseUrl: String)
}