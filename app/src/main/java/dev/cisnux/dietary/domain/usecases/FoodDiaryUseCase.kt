package dev.cisnux.dietary.domain.usecases

import dev.cisnux.dietary.domain.models.AddFoodDiary
import dev.cisnux.dietary.domain.models.FoodDiary
import dev.cisnux.dietary.domain.models.FoodDiaryResult
import dev.cisnux.dietary.utils.DiaryFoodCategory
import dev.cisnux.dietary.utils.UiState
import kotlinx.coroutines.flow.Flow

interface FoodDiaryUseCase {
    suspend fun getDiaryFoodsByDays(days: Long, category: DiaryFoodCategory): Flow<UiState<List<FoodDiary>>>
    suspend fun getDiaryFoodsByQuery(query: String): Flow<UiState<List<FoodDiary>>>
    suspend fun getKeywordSuggestionsByQuery(query: String): Flow<UiState<List<String>>>
    suspend fun addFoodDiary(addFoodDiary: AddFoodDiary): Flow<UiState<FoodDiaryResult>>
}