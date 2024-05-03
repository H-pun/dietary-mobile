package dev.cisnux.dietary.domain.usecases

import dev.cisnux.dietary.domain.models.AddFoodDiary
import dev.cisnux.dietary.domain.models.FoodDiary
import dev.cisnux.dietary.domain.models.FoodDiaryDetail
import dev.cisnux.dietary.domain.models.FoodNutrition
import dev.cisnux.dietary.domain.models.UserNutrition
import dev.cisnux.dietary.domain.models.Report
import dev.cisnux.dietary.domain.repositories.FoodRepository
import dev.cisnux.dietary.utils.ACTIVITY_FACTOR
import dev.cisnux.dietary.utils.Failure
import dev.cisnux.dietary.utils.FoodDiaryCategory
import dev.cisnux.dietary.utils.GOALS_FACTOR
import dev.cisnux.dietary.utils.ReportCategory
import dev.cisnux.dietary.utils.UiState
import dev.cisnux.dietary.utils.currentLocalDateTimeInBasicISOFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.io.File
import java.time.Instant
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class FoodDiaryInteractor @Inject constructor(
    private val foodRepository: FoodRepository,
    private val authenticationUseCase: AuthenticationUseCase,
    private val userProfileUseCase: UserProfileUseCase,
) : FoodDiaryUseCase {
    override fun getDiaryFoodsByDays(
        date: String, category: FoodDiaryCategory
    ): Flow<UiState<List<FoodDiary>>> =
        authenticationUseCase.isAccessTokenAndUserIdExists.flatMapLatest {
            it?.let {
                foodRepository.getDiaryFoodsByDate(
                    userId = it.first, accessToken = it.second, date = date, category = category
                )
            } ?: flow { emit(UiState.Error(Failure.UnauthorizedFailure())) }
        }

    override fun getDiaryFoodsByQuery(query: String): Flow<UiState<List<FoodDiary>>> =
        foodRepository.getDiaryFoodsByQuery(query)

    override fun getKeywordSuggestionsByQuery(query: String): Flow<UiState<List<String>>> =
        foodRepository.getKeywordSuggestionsByQuery(query)

    override fun addFoodDiary(addFoodDiary: AddFoodDiary): Flow<UiState<String>> =
        authenticationUseCase.isAccessTokenAndUserIdExists.flatMapLatest {
            it?.let {
                foodRepository.addFoodDiary(
                    userId = it.first, accessToken = it.second, addFoodDiary = addFoodDiary
                )
            } ?: flow { emit(UiState.Error(Failure.UnauthorizedFailure())) }
        }

    override fun getFoodDiaryDetailById(foodDiaryId: String): Flow<UiState<FoodDiaryDetail>> =
        authenticationUseCase.isAccessTokenAndUserIdExists.flatMapLatest {
            it?.let {
                foodRepository.getFoodDiaryDetailById(
                    accessToken = it.second,
                    foodDiaryId = foodDiaryId
                )
            } ?: flow { emit(UiState.Error(Failure.UnauthorizedFailure())) }
        }.flowOn(Dispatchers.IO)

    override fun deleteFoodDiaryById(foodDiaryId: String): Flow<UiState<Nothing>> =
        authenticationUseCase.accessToken.flatMapLatest {
            it?.let { accessToken ->
                foodRepository.deleteFoodDiaryById(accessToken, foodDiaryId)
            } ?: flow { emit(UiState.Error(Failure.UnauthorizedFailure())) }
        }.flowOn(Dispatchers.IO)

    override fun getFoodDiaryReports(category: ReportCategory): Flow<UiState<Report>> =
        foodRepository.getFoodDiaryReports(category)

    override fun predictFoods(foodPicture: File): Flow<UiState<Pair<UserNutrition, FoodNutrition>>> =
        userProfileUseCase.userProfileDetail.combine(authenticationUseCase.accessToken) { userProfileDetail, accessToken ->
            Pair(first = accessToken, second = userProfileDetail)
        }
            .flatMapLatest {
                it.first?.let { accessToken ->
                    foodRepository.predictFood(
                        userId = it.second.userAccountId,
                        accessToken = accessToken,
                        foodPicture = foodPicture,
                        date = Instant.now().currentLocalDateTimeInBasicISOFormat
                    )
                        .map { uiState ->
                            if (uiState is UiState.Success) {
                                val height = it.second.height
                                val weight = it.second.weight
                                val age = it.second.age
                                val isWoman = it.second.gender.lowercase() == "wanita"
                                val bmr = if (!isWoman)
                                    66 + (13.7 * weight) + (5 * height) - (6.8 * age)
                                else 655 + (9.6 * weight) + (1.8 * height) - (4.7 * age)
                                val activityFactor =
                                    ACTIVITY_FACTOR[it.second.activityLevel.lowercase()] ?: 1.0
                                val goalsFactor = GOALS_FACTOR[it.second.goal] ?: 1.0
                                val stressFactor = 1.0
                                val userMaxDailyCalories =
                                    (bmr * activityFactor * goalsFactor * stressFactor).toFloat()
                                val userMaxDailyProtein = 0.15f * userMaxDailyCalories
                                val userMaxDailyFat = 0.25f * userMaxDailyCalories
                                val userMaxDailyCarbohydrate = 0.6f * userMaxDailyCalories
                                val currentDataState = uiState.data!!

                                UiState.Success(
                                    data = currentDataState.copy(
                                        first = currentDataState.first.copy(
                                            maxDailyCalories = userMaxDailyCalories,
                                            maxDailyProtein = userMaxDailyProtein,
                                            maxDailyFat = userMaxDailyFat,
                                            maxDailyCarbohydrate = userMaxDailyCarbohydrate,
                                        ),
                                        second = currentDataState.second.copy(
                                            totalCalories = currentDataState.second.foods.sumOf { predictedFood -> predictedFood.calories.toDouble() }
                                                .toFloat(),
                                            totalProtein = currentDataState.second.foods.sumOf { predictedFood -> predictedFood.protein.toDouble() }
                                                .toFloat(),
                                            totalFat = currentDataState.second.foods.sumOf { predictedFood -> predictedFood.fat.toDouble() }
                                                .toFloat(),
                                            totalCarbohydrate = currentDataState.second.foods.sumOf { predictedFood -> predictedFood.carbohydrates.toDouble() }
                                                .toFloat(),
                                        )
                                    )
                                )
                            } else uiState
                        }.flowOn(Dispatchers.Default)
                } ?: flow { emit(UiState.Error(Failure.UnauthorizedFailure())) }
            }
            .flowOn(Dispatchers.IO)

    override val baseUrl: Flow<String>
        get() = foodRepository.baseUrl

    override suspend fun updateBaseUrlApi(baseUrl: String) =
        foodRepository.updateBaseUrlApi(baseUrl = baseUrl)
}