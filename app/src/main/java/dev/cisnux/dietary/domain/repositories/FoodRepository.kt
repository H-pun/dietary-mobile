package dev.cisnux.dietary.domain.repositories

import dev.cisnux.dietary.domain.models.FoodDiary
import dev.cisnux.dietary.utils.DiaryFoodCategory
import dev.cisnux.dietary.utils.UiState
import kotlinx.coroutines.flow.Flow

interface FoodRepository {
    fun getDiaryFoodsByDays(days: Long, category: DiaryFoodCategory): Flow<UiState<List<FoodDiary>>>
    fun getDiaryFoodsByQuery(query: String): Flow<UiState<List<FoodDiary>>>
    fun getKeywordSuggestionsByQuery(query: String): Flow<UiState<List<String>>>
}
