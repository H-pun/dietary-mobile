package org.cisnux.mydietary.domain.usecases

import kotlinx.coroutines.flow.Flow
import org.cisnux.mydietary.domain.models.AddFoodDiary
import org.cisnux.mydietary.domain.models.FoodDiary
import org.cisnux.mydietary.domain.models.FoodDiaryDetail
import org.cisnux.mydietary.domain.models.FoodNutrition
import org.cisnux.mydietary.domain.models.Keyword
import org.cisnux.mydietary.domain.models.Report
import org.cisnux.mydietary.utils.FoodDiaryCategory
import org.cisnux.mydietary.utils.ReportCategory
import org.cisnux.mydietary.utils.UiState
import java.io.File

interface FoodDiaryUseCase {
    fun getDiaryFoodsByDaysAndCategory(date: String, category: FoodDiaryCategory): Flow<UiState<List<FoodDiary>>>
    fun getDiaryFoodsByDaysForWidget(date: String): Flow<UiState<List<FoodDiary>>>
    fun getDiaryFoodsByQuery(query: String): Flow<UiState<List<FoodDiary>>>
    fun getKeywordSuggestionsByQuery(query: String): Flow<UiState<List<Keyword>>>
    fun addFoodDiary(addFoodDiary: AddFoodDiary): Flow<UiState<String>>
    fun getFoodDiaryDetailById(foodDiaryId: String): Flow<UiState<FoodDiaryDetail>>

    fun deleteFoodDiaryById(foodDiaryId: String): Flow<UiState<Nothing>>

    fun getFoodDiaryReports(category: ReportCategory): Flow<UiState<Report>>
    fun predictFoods(foodPicture: File): Flow<UiState<FoodNutrition>>

    val baseUrl: Flow<String>
    suspend fun updateBaseUrlApi(baseUrl: String)
}