package dev.cisnux.dietary.domain.repositories

import dev.cisnux.dietary.domain.models.DiaryFood
import dev.cisnux.dietary.utils.DiaryFoodCategory
import dev.cisnux.dietary.utils.UiState
import kotlinx.coroutines.flow.Flow

interface FoodRepository {
    fun getDiaryFoodsByDays(days: Long, category: DiaryFoodCategory): Flow<UiState<List<DiaryFood>>>
    fun getDiaryFoodsByQuery(query: String): Flow<UiState<List<DiaryFood>>>
    fun getKeywordSuggestionsByQuery(query: String): Flow<UiState<List<String>>>
}
