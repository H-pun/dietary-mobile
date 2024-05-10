package org.cisnux.mydietary.domain.usecases

import org.cisnux.mydietary.domain.models.AddFoodDiary
import org.cisnux.mydietary.domain.models.FoodDiary
import org.cisnux.mydietary.domain.models.FoodDiaryDetail
import org.cisnux.mydietary.domain.models.FoodNutrition
import org.cisnux.mydietary.domain.models.UserNutrition
import org.cisnux.mydietary.domain.repositories.FoodRepository
import org.cisnux.mydietary.utils.ACTIVITY_FACTOR
import org.cisnux.mydietary.utils.Failure
import org.cisnux.mydietary.utils.FoodDiaryCategory
import org.cisnux.mydietary.utils.GOALS_FACTOR
import org.cisnux.mydietary.utils.ReportCategory
import org.cisnux.mydietary.utils.UiState
import org.cisnux.mydietary.utils.currentLocalDateTimeInBasicISOFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.cisnux.mydietary.domain.models.FoodDiaryReport
import org.cisnux.mydietary.domain.models.Keyword
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

    override fun getDiaryFoodsByDaysOnly(date: String): Flow<UiState<List<FoodDiary>>> =
        authenticationUseCase.isAccessTokenAndUserIdExists.flatMapLatest {
            it?.let {
                foodRepository.getDiaryFoodsByDate(
                    userId = it.first, accessToken = it.second, date = date
                )
            } ?: flow { emit(UiState.Error(Failure.UnauthorizedFailure())) }
        }

    override fun getDiaryFoodsByQuery(query: String): Flow<UiState<List<FoodDiary>>> =
        foodRepository.getDiaryFoodsByQuery(query)

    override fun getKeywordSuggestionsByQuery(query: String): Flow<UiState<List<Keyword>>> =
        authenticationUseCase.isAccessTokenAndUserIdExists.flatMapLatest {
            it?.let {
                foodRepository.getKeywordSuggestions(
                    userId = it.first, accessToken = it.second
                )
                    .map { uiState ->
                        if (uiState is UiState.Success)
                            UiState.Success(data = uiState.data?.filter { keyword ->
                                keyword.text.lowercase().contains(query.lowercase())
                            })
                        else uiState
                    }.flowOn(Dispatchers.Default)
            } ?: flow { emit(UiState.Error(Failure.UnauthorizedFailure())) }
        }.flowOn(Dispatchers.IO)

    override fun addFoodDiary(addFoodDiary: AddFoodDiary): Flow<UiState<String>> =
        authenticationUseCase.isAccessTokenAndUserIdExists.flatMapLatest {
            it?.let {
                foodRepository.addFoodDiary(
                    userId = it.first, accessToken = it.second, addFoodDiary = addFoodDiary
                )
            } ?: flow { emit(UiState.Error(Failure.UnauthorizedFailure())) }
        }

    override fun getFoodDiaryDetailById(foodDiaryId: String): Flow<UiState<FoodDiaryDetail>> =
        authenticationUseCase.accessToken.flatMapLatest {
            it?.let { accessToken ->
                foodRepository.getFoodDiaryDetailById(
                    accessToken = accessToken,
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

    override fun getFoodDiaryReports(category: ReportCategory): Flow<UiState<List<FoodDiaryReport>>> =
        authenticationUseCase.isAccessTokenAndUserIdExists.distinctUntilChanged().flatMapLatest {
            it?.let {
                foodRepository.getFoodDiaryReports(
                    accessToken = it.second,
                    userId = it.first,
                    category = category
                )
            } ?: flow { emit(UiState.Error(Failure.UnauthorizedFailure())) }
        }.flowOn(Dispatchers.IO).distinctUntilChanged()

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
                                        second = currentDataState.second
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