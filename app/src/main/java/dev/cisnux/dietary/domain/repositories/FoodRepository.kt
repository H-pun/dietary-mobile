package dev.cisnux.dietary.domain.repositories

import dev.cisnux.dietary.domain.models.AddFoodDiary
import dev.cisnux.dietary.domain.models.FoodDiary
import dev.cisnux.dietary.domain.models.FoodDiaryQuestion
import dev.cisnux.dietary.domain.models.FoodDiaryDetail
import dev.cisnux.dietary.domain.models.Report
import dev.cisnux.dietary.utils.FoodDiaryCategory
import dev.cisnux.dietary.utils.ReportCategory
import dev.cisnux.dietary.utils.UiState
import kotlinx.coroutines.flow.Flow

interface FoodRepository {
    fun getDiaryFoodsByDays(days: Long, category: FoodDiaryCategory): Flow<UiState<List<FoodDiary>>>
    fun getDiaryFoodsByQuery(query: String): Flow<UiState<List<FoodDiary>>>
    fun getKeywordSuggestionsByQuery(query: String): Flow<UiState<List<String>>>
    fun addFoodDiary(addFoodDiary: AddFoodDiary): Flow<UiState<FoodDiaryDetail>>
    fun updateFoodDiaryBaseOnAnsweredQuestion(foodDiaryQuestion: FoodDiaryQuestion): Flow<UiState<FoodDiaryDetail>>
    fun deleteFoodDiaryById(foodDiaryId: String): Flow<UiState<Nothing>>
    fun getFoodDiaryDetailById(foodDiaryId: String): Flow<UiState<FoodDiaryDetail>>
    fun duplicateFoodDiaryById(foodDiaryId: String, foodDiaryCategory: String): Flow<UiState<Nothing>>
    fun getFoodDiaryReports(category: ReportCategory): Flow<UiState<Report>>
}
