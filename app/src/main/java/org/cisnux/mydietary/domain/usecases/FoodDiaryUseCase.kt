package org.cisnux.mydietary.domain.usecases

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import org.cisnux.mydietary.domain.models.AddFoodDiary
import org.cisnux.mydietary.domain.models.FoodDiary
import org.cisnux.mydietary.domain.models.FoodDiaryDetail
import org.cisnux.mydietary.domain.models.FoodNutrition
import org.cisnux.mydietary.domain.models.Keyword
import org.cisnux.mydietary.commons.utils.FoodDiaryCategory
import org.cisnux.mydietary.commons.utils.UiState
import java.io.File

interface FoodDiaryUseCase {
    fun getFoodDiariesByDaysAndCategory(
        date: String,
        category: FoodDiaryCategory,
        scope: CoroutineScope
    ): Flow<UiState<List<FoodDiary>>>

    fun getFoodDiariesByDaysForWidget(
        date: String,
        scope: CoroutineScope = CoroutineScope(context = SupervisorJob() + Dispatchers.IO)
    ): Flow<UiState<List<FoodDiary>>>

    fun getFoodDiariesByQuery(query: String, scope: CoroutineScope): Flow<UiState<List<FoodDiary>>>
    fun getSuggestionKeywordsByQuery(
        query: String,
        scope: CoroutineScope
    ): Flow<UiState<List<Keyword>>>

    fun addFoodDiary(addFoodDiary: AddFoodDiary, scope: CoroutineScope): Flow<UiState<String>>
    fun getFoodDiaryDetailById(
        foodDiaryId: String,
        scope: CoroutineScope
    ): Flow<UiState<FoodDiaryDetail>>

    fun deleteFoodDiaryById(foodDiaryId: String, scope: CoroutineScope): Flow<UiState<Nothing>>

    fun predictFoods(foodPicture: File, scope: CoroutineScope): Flow<UiState<FoodNutrition>>

    val baseUrl: Flow<String>
    suspend fun updateBaseUrlApi(baseUrl: String)
}