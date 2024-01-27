package dev.cisnux.dietary.domain.usecases

import dev.cisnux.dietary.domain.models.DiaryFood
import dev.cisnux.dietary.utils.DiaryFoodCategory
import dev.cisnux.dietary.utils.UiState
import kotlinx.coroutines.flow.Flow

interface DiaryFoodUseCase {
    suspend fun getDiaryFoodsByDays(days: Long, category: DiaryFoodCategory): Flow<UiState<List<DiaryFood>>>
    suspend fun getDiaryFoodsByQuery(query: String): Flow<UiState<List<DiaryFood>>>
    suspend fun getKeywordSuggestionsByQuery(query: String): Flow<UiState<List<String>>>
}