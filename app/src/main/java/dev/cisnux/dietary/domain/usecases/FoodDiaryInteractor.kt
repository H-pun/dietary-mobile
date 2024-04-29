package dev.cisnux.dietary.domain.usecases

import dev.cisnux.dietary.domain.models.AddFoodDiary
import dev.cisnux.dietary.domain.models.FoodDiary
import dev.cisnux.dietary.domain.models.FoodDiaryQuestion
import dev.cisnux.dietary.domain.models.FoodDiaryDetail
import dev.cisnux.dietary.domain.models.PredictedFood
import dev.cisnux.dietary.domain.models.Report
import dev.cisnux.dietary.domain.repositories.FoodRepository
import dev.cisnux.dietary.utils.Failure
import dev.cisnux.dietary.utils.FoodDiaryCategory
import dev.cisnux.dietary.utils.ReportCategory
import dev.cisnux.dietary.utils.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class FoodDiaryInteractor @Inject constructor(
    private val foodRepository: FoodRepository,
    private val authenticationUseCase: AuthenticationUseCase
) : FoodDiaryUseCase {
    override fun getDiaryFoodsByDays(
        date: String, category: FoodDiaryCategory
    ): Flow<UiState<List<FoodDiary>>> =
        authenticationUseCase.isAccessTokenAndUserIdExists.flatMapLatest {
            it?.let {
                foodRepository.getDiaryFoodsByDays(
                    userId = it.first, accessToken = it.second, date = date, category = category
                )
            } ?: flow { emit(UiState.Error(Failure.UnauthorizedFailure())) }
        }

    override fun getDiaryFoodsByQuery(query: String): Flow<UiState<List<FoodDiary>>> =
        foodRepository.getDiaryFoodsByQuery(query)

    override fun getKeywordSuggestionsByQuery(query: String): Flow<UiState<List<String>>> =
        foodRepository.getKeywordSuggestionsByQuery(query)

    override fun addFoodDiary(addFoodDiary: AddFoodDiary): Flow<UiState<FoodDiaryDetail>> =
        authenticationUseCase.isAccessTokenAndUserIdExists.flatMapLatest {
            it?.let {
                foodRepository.addFoodDiary(
                    userId = it.first, accessToken = it.second, addFoodDiary = addFoodDiary
                )
            } ?: flow { emit(UiState.Error(Failure.UnauthorizedFailure())) }
        }

    override fun getFoodDiaryDetailById(foodDiaryId: String): Flow<UiState<FoodDiaryDetail>> =
        foodRepository.getFoodDiaryDetailById(foodDiaryId)

    override fun updateFoodDiaryBaseOnAnsweredQuestion(foodDiaryQuestion: FoodDiaryQuestion): Flow<UiState<FoodDiaryDetail>> =
        foodRepository.updateFoodDiaryBaseOnAnsweredQuestion(foodDiaryQuestion)

    override fun deleteFoodDiaryById(foodDiaryId: String): Flow<UiState<Nothing>> =
        foodRepository.deleteFoodDiaryById(foodDiaryId)

    override fun getFoodDiaryReports(category: ReportCategory): Flow<UiState<Report>> =
        foodRepository.getFoodDiaryReports(category)

    override fun predictFoods(foodPicture: File): Flow<UiState<List<PredictedFood>>> =
        authenticationUseCase.accessToken.flatMapLatest {
            it?.let { accessToken ->
                foodRepository.predictFood(accessToken = accessToken, foodPicture = foodPicture)
            } ?: flow { emit(UiState.Error(Failure.UnauthorizedFailure())) }
        }

    override val baseUrl: Flow<String>
        get() = foodRepository.baseUrl

    override suspend fun updateBaseUrlApi(baseUrl: String) =
        foodRepository.updateBaseUrlApi(baseUrl = baseUrl)
}