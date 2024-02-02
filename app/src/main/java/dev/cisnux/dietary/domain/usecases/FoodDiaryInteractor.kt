package dev.cisnux.dietary.domain.usecases

import dev.cisnux.dietary.domain.models.AddFoodDiary
import dev.cisnux.dietary.domain.models.FoodDiary
import dev.cisnux.dietary.domain.models.FoodDiaryQuestion
import dev.cisnux.dietary.domain.models.FoodDiaryDetail
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
    override suspend fun addFoodDiary(addFoodDiary: AddFoodDiary): Flow<UiState<FoodDiaryDetail>> = foodRepository.addFoodDiary(addFoodDiary)
    override suspend fun getFoodDiaryDetailById(foodDiaryId: String): Flow<UiState<FoodDiaryDetail>> = foodRepository.getFoodDiaryDetailById(foodDiaryId)

    override suspend fun updateFoodDiaryBaseOnAnsweredQuestion(foodDiaryQuestion: FoodDiaryQuestion): Flow<UiState<FoodDiaryDetail>> =
        foodRepository.updateFoodDiaryBaseOnAnsweredQuestion(foodDiaryQuestion)

    override suspend fun deleteFoodDiaryById(foodDiaryId: String): Flow<UiState<Nothing>> = foodRepository.deleteFoodDiaryById(foodDiaryId)
    override suspend fun duplicateFoodDiaryById(
        foodDiaryId: String,
        foodDiaryCategory: String
    ): Flow<UiState<Nothing>> = foodRepository.duplicateFoodDiaryById(
        foodDiaryId,
        foodDiaryCategory
    )
}