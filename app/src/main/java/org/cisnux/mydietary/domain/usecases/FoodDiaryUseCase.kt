package org.cisnux.mydietary.domain.usecases

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.cisnux.mydietary.domain.models.AddFoodDiary
import org.cisnux.mydietary.domain.models.FoodDiary
import org.cisnux.mydietary.domain.models.FoodDiaryDetail
import org.cisnux.mydietary.domain.models.FoodNutrition
import org.cisnux.mydietary.domain.models.Keyword
import org.cisnux.mydietary.domain.models.WeeklyNutritionReport
import org.cisnux.mydietary.utils.FoodDiaryCategory
import org.cisnux.mydietary.utils.ReportCategory
import org.cisnux.mydietary.utils.UiState
import java.io.File

interface FoodDiaryUseCase {
    fun getDiaryFoodsByDaysAndCategory(date: String, category: FoodDiaryCategory, scope: CoroutineScope): Flow<UiState<List<FoodDiary>>>
    fun getDiaryFoodsByDaysForWidget(date: String, scope: CoroutineScope): Flow<UiState<List<FoodDiary>>>
    fun getDiaryFoodsByQuery(query: String, scope: CoroutineScope): Flow<UiState<List<FoodDiary>>>
    fun getKeywordSuggestionsByQuery(query: String, scope: CoroutineScope): Flow<UiState<List<Keyword>>>
    fun addFoodDiary(addFoodDiary: AddFoodDiary, scope: CoroutineScope): Flow<UiState<String>>
    fun getFoodDiaryDetailById(foodDiaryId: String, scope: CoroutineScope): Flow<UiState<FoodDiaryDetail>>

    fun deleteFoodDiaryById(foodDiaryId: String, scope: CoroutineScope): Flow<UiState<Nothing>>

    fun predictFoods(foodPicture: File, scope: CoroutineScope): Flow<UiState<FoodNutrition>>

    val baseUrl: Flow<String>
    suspend fun updateBaseUrlApi(baseUrl: String)
}