package org.cisnux.mydietary.domain.repositories

import org.cisnux.mydietary.domain.models.AddFoodDiary
import org.cisnux.mydietary.domain.models.FoodDiary
import org.cisnux.mydietary.domain.models.FoodDiaryDetail
import org.cisnux.mydietary.domain.models.FoodNutrition
import org.cisnux.mydietary.domain.models.UserNutrition
import org.cisnux.mydietary.domain.models.Report
import org.cisnux.mydietary.utils.FoodDiaryCategory
import org.cisnux.mydietary.utils.ReportCategory
import org.cisnux.mydietary.utils.UiState
import kotlinx.coroutines.flow.Flow
import org.cisnux.mydietary.domain.models.Keyword
import java.io.File

interface FoodRepository {
    val baseUrl: Flow<String>
    fun getDiaryFoodsByDate(
        accessToken: String,
        userId: String,
        date: String,
        category: FoodDiaryCategory
    ): Flow<UiState<List<FoodDiary>>>

    fun getDiaryFoodsByQuery(query: String): Flow<UiState<List<FoodDiary>>>

    fun getKeywordSuggestions(accessToken: String, userId: String): Flow<UiState<List<Keyword>>>

    fun addFoodDiary(
        accessToken: String,
        userId: String,
        addFoodDiary: AddFoodDiary
    ): Flow<UiState<String>>

    fun predictFood(
        userId: String,
        accessToken: String,
        foodPicture: File,
        date: String
    ): Flow<UiState<Pair<UserNutrition, FoodNutrition>>>

    fun deleteFoodDiaryById(accessToken: String, foodDiaryId: String): Flow<UiState<Nothing>>
    fun getFoodDiaryDetailById(
        accessToken: String,
        foodDiaryId: String
    ): Flow<UiState<FoodDiaryDetail>>

    fun getFoodDiaryReports(category: ReportCategory): Flow<UiState<Report>>
    suspend fun updateBaseUrlApi(baseUrl: String)
}
