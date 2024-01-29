package dev.cisnux.dietary.domain.usecases

import dev.cisnux.dietary.domain.models.FoodDiary
import dev.cisnux.dietary.domain.repositories.FoodRepository
import dev.cisnux.dietary.utils.DiaryFoodCategory
import dev.cisnux.dietary.utils.UiState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FoodDiaryInteractor @Inject constructor(
    private val foodRepository: FoodRepository
) : FoodDiaryUseCase {
    override suspend fun getDiaryFoodsByDays(days: Long, category: DiaryFoodCategory): Flow<UiState<List<FoodDiary>>> =
        foodRepository.getDiaryFoodsByDays(days, category)

    override suspend fun getDiaryFoodsByQuery(query: String): Flow<UiState<List<FoodDiary>>> = foodRepository.getDiaryFoodsByQuery(query)
    override suspend fun getKeywordSuggestionsByQuery(query: String): Flow<UiState<List<String>>> = foodRepository.getKeywordSuggestionsByQuery(query)

}