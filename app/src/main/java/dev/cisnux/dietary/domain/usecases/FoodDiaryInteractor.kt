package dev.cisnux.dietary.domain.usecases

import dev.cisnux.dietary.domain.models.AddFoodDiary
import dev.cisnux.dietary.domain.models.FoodDiary
import dev.cisnux.dietary.domain.models.FoodDiaryQuestion
import dev.cisnux.dietary.domain.models.FoodDiaryDetail
import dev.cisnux.dietary.domain.models.PredictedFood
import dev.cisnux.dietary.domain.models.Report
import dev.cisnux.dietary.domain.repositories.FoodRepository
import dev.cisnux.dietary.utils.FoodDiaryCategory
import dev.cisnux.dietary.utils.ReportCategory
import dev.cisnux.dietary.utils.UiState
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

class FoodDiaryInteractor @Inject constructor(
    private val foodRepository: FoodRepository
) : FoodDiaryUseCase {
    override fun getDiaryFoodsByDays(
        date: String,
        category: FoodDiaryCategory
    ): Flow<UiState<List<FoodDiary>>> =
        foodRepository.getDiaryFoodsByDays(date, category)

    override fun getDiaryFoodsByQuery(query: String): Flow<UiState<List<FoodDiary>>> =
        foodRepository.getDiaryFoodsByQuery(query)

    override fun getKeywordSuggestionsByQuery(query: String): Flow<UiState<List<String>>> =
        foodRepository.getKeywordSuggestionsByQuery(query)

    override fun addFoodDiary(addFoodDiary: AddFoodDiary): Flow<UiState<FoodDiaryDetail>> =
        foodRepository.addFoodDiary(addFoodDiary)

    override fun getFoodDiaryDetailById(foodDiaryId: String): Flow<UiState<FoodDiaryDetail>> =
        foodRepository.getFoodDiaryDetailById(foodDiaryId)

    override fun updateFoodDiaryBaseOnAnsweredQuestion(foodDiaryQuestion: FoodDiaryQuestion): Flow<UiState<FoodDiaryDetail>> =
        foodRepository.updateFoodDiaryBaseOnAnsweredQuestion(foodDiaryQuestion)

    override fun deleteFoodDiaryById(foodDiaryId: String): Flow<UiState<Nothing>> =
        foodRepository.deleteFoodDiaryById(foodDiaryId)

    override fun getFoodDiaryReports(category: ReportCategory): Flow<UiState<Report>> =
        foodRepository.getFoodDiaryReports(category)

    override fun predictFoods(foodPicture: File): Flow<UiState<List<PredictedFood>>> =
        foodRepository.predictFood(foodPicture)
}