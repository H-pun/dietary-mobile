package dev.cisnux.dietary.domain.usecases

import dev.cisnux.dietary.domain.models.AddFoodDiary
import dev.cisnux.dietary.domain.models.FoodDiary
import dev.cisnux.dietary.domain.models.FoodDiaryQuestion
import dev.cisnux.dietary.domain.models.FoodDiaryDetail
import dev.cisnux.dietary.domain.models.Report
import dev.cisnux.dietary.domain.repositories.FoodRepository
import dev.cisnux.dietary.utils.FoodDiaryCategory
import dev.cisnux.dietary.utils.ReportCategory
import dev.cisnux.dietary.utils.UiState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FoodDiaryInteractor @Inject constructor(
    private val foodRepository: FoodRepository
) : FoodDiaryUseCase {
    override fun getDiaryFoodsByDays(days: Long, category: FoodDiaryCategory): Flow<UiState<List<FoodDiary>>> =
        foodRepository.getDiaryFoodsByDays(days, category)

    override fun getDiaryFoodsByQuery(query: String): Flow<UiState<List<FoodDiary>>> = foodRepository.getDiaryFoodsByQuery(query)
    override fun getKeywordSuggestionsByQuery(query: String): Flow<UiState<List<String>>> = foodRepository.getKeywordSuggestionsByQuery(query)
    override fun addFoodDiary(addFoodDiary: AddFoodDiary): Flow<UiState<FoodDiaryDetail>> = foodRepository.addFoodDiary(addFoodDiary)
    override fun getFoodDiaryDetailById(foodDiaryId: String): Flow<UiState<FoodDiaryDetail>> = foodRepository.getFoodDiaryDetailById(foodDiaryId)

    override fun updateFoodDiaryBaseOnAnsweredQuestion(foodDiaryQuestion: FoodDiaryQuestion): Flow<UiState<FoodDiaryDetail>> =
        foodRepository.updateFoodDiaryBaseOnAnsweredQuestion(foodDiaryQuestion)

    override fun deleteFoodDiaryById(foodDiaryId: String): Flow<UiState<Nothing>> = foodRepository.deleteFoodDiaryById(foodDiaryId)
    override fun duplicateFoodDiaryById(
        foodDiaryId: String,
        foodDiaryCategory: String
    ): Flow<UiState<Nothing>> = foodRepository.duplicateFoodDiaryById(
        foodDiaryId,
        foodDiaryCategory
    )

    override fun getFoodDiaryReports(category: ReportCategory): Flow<UiState<Report>> =
        foodRepository.getFoodDiaryReports(category)
}