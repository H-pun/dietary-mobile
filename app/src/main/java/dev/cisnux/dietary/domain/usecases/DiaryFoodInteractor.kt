package dev.cisnux.dietary.domain.usecases

import dev.cisnux.dietary.domain.models.DiaryFood
import dev.cisnux.dietary.domain.repositories.FoodRepository
import dev.cisnux.dietary.utils.DiaryFoodCategory
import dev.cisnux.dietary.utils.UiState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DiaryFoodInteractor @Inject constructor(
    private val foodRepository: FoodRepository
) : DiaryFoodUseCase {
    override suspend fun getDiaryFoodsByDays(days: Long, category: DiaryFoodCategory): Flow<UiState<List<DiaryFood>>> =
        foodRepository.getDiaryFoodsByDays(days, category)

    override suspend fun getDiaryFoodsByQuery(query: String): Flow<UiState<List<DiaryFood>>> = foodRepository.getDiaryFoodsByQuery(query)
    override suspend fun getKeywordSuggestionsByQuery(query: String): Flow<UiState<List<String>>> = foodRepository.getKeywordSuggestionsByQuery(query)

}