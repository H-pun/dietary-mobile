package org.cisnux.mydietary.domain.usecases

import android.content.Context
import androidx.core.graphics.drawable.toBitmapOrNull
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.request.SuccessResult
import dagger.hilt.android.qualifiers.ApplicationContext
import org.cisnux.mydietary.domain.models.AddFoodDiary
import org.cisnux.mydietary.domain.models.FoodDiary
import org.cisnux.mydietary.domain.models.FoodDiaryDetail
import org.cisnux.mydietary.domain.models.FoodNutrition
import org.cisnux.mydietary.domain.models.UserNutrition
import org.cisnux.mydietary.domain.repositories.FoodRepository
import org.cisnux.mydietary.utils.Failure
import org.cisnux.mydietary.utils.FoodDiaryCategory
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
import org.cisnux.mydietary.domain.models.Keyword
import org.cisnux.mydietary.domain.models.Report
import org.cisnux.mydietary.utils.calculateMaxDailyNutrition
import java.io.File
import java.time.Instant
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class FoodDiaryInteractor @Inject constructor(
    private val foodRepository: FoodRepository,
    private val authenticationUseCase: AuthenticationUseCase,
    private val userProfileUseCase: UserProfileUseCase,
    @ApplicationContext private val context: Context
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

    override fun getDiaryFoodsByDaysForWidget(date: String): Flow<UiState<List<FoodDiary>>> =
        authenticationUseCase.isAccessTokenAndUserIdExists.flatMapLatest {
            it?.let {
                foodRepository.getDiaryFoodsByDate(
                    userId = it.first, accessToken = it.second, date = date
                ).map { uiState ->
                    if (uiState is UiState.Success)
                        uiState.copy(data = uiState.data?.map { foodDiary ->
                            val imageLoader = ImageLoader(context)
                            val request = ImageRequest.Builder(context)
                                .allowConversionToBitmap(true)
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .data(foodDiary.foodPictureUrl)
                                .dispatcher(Dispatchers.IO)
                                .build()

                            val result = imageLoader.execute(request)

                            FoodDiary(
                                id = foodDiary.id,
                                title = foodDiary.title,
                                date = foodDiary.date,
                                time = foodDiary.time,
                                foodPictureUrl = foodDiary.foodPictureUrl,
                                totalFoodCalories = foodDiary.totalFoodCalories,
                                foodPictureFile = if (result is SuccessResult) result.drawable.toBitmapOrNull()
                                else null
                            )
                        } ?: listOf())
                    else uiState
                }.flowOn(Dispatchers.IO)
            } ?: flow { emit(UiState.Error(Failure.UnauthorizedFailure())) }
        }

    override fun getDiaryFoodsByQuery(query: String): Flow<UiState<List<FoodDiary>>> =
        authenticationUseCase.isAccessTokenAndUserIdExists.flatMapLatest {
            it?.let {
                foodRepository.getDiaryFoodsByQuery(
                    userId = it.first,
                    accessToken = it.second,
                    query = query
                )
            } ?: flow { emit(UiState.Error(Failure.UnauthorizedFailure())) }
        }

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

    @OptIn(ExperimentalCoilApi::class)
    override fun getFoodDiaryDetailById(foodDiaryId: String): Flow<UiState<FoodDiaryDetail>> =
        authenticationUseCase.accessToken.flatMapLatest {
            it?.let { accessToken ->
                foodRepository.getFoodDiaryDetailById(
                    accessToken = accessToken,
                    foodDiaryId = foodDiaryId
                ).map { uiState ->
                    if (uiState is UiState.Success) {
                        val image = uiState.data?.foodNutrition?.image as String?
                        val imageLoader = ImageLoader(context)
                        val imageRequest = ImageRequest.Builder(context)
                            .allowConversionToBitmap(true)
                            .dispatcher(Dispatchers.IO)
                            .data(image).build()
                        imageLoader
                            .execute(imageRequest)
                        val path =
                            image?.let { img -> imageLoader.diskCache?.openSnapshot(img)?.data }

                        uiState.copy(
                            data = uiState.data?.copy(
                                foodNutrition = uiState.data.foodNutrition.copy(image = path?.toFile())
                            )
                        )
                    } else uiState
                }
            } ?: flow { emit(UiState.Error(Failure.UnauthorizedFailure())) }
        }.flowOn(Dispatchers.IO)

    override fun deleteFoodDiaryById(foodDiaryId: String): Flow<UiState<Nothing>> =
        authenticationUseCase.accessToken.flatMapLatest {
            it?.let { accessToken ->
                foodRepository.deleteFoodDiaryById(accessToken, foodDiaryId)
            } ?: flow { emit(UiState.Error(Failure.UnauthorizedFailure())) }
        }.flowOn(Dispatchers.IO)

    override fun getFoodDiaryReports(
        category: ReportCategory,
    ): Flow<UiState<Report>> =
        authenticationUseCase.isAccessTokenAndUserIdExists.distinctUntilChanged().flatMapLatest {
            it?.let {
                foodRepository.getFoodDiaryReports(
                    accessToken = it.second,
                    userId = it.first,
                    category = category
                )
            } ?: flow { emit(UiState.Error(Failure.UnauthorizedFailure())) }
        }.flowOn(Dispatchers.IO).distinctUntilChanged()

    @OptIn(ExperimentalCoilApi::class)
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
                                val currentDataState = uiState.data!!
                                // user profile
                                val userProfile = it.second
                                val userNutrition = currentDataState.first

                                val image = currentDataState.second.image as String?
                                val imageLoader = ImageLoader(context)
                                val imageRequest = ImageRequest.Builder(context)
                                    .allowConversionToBitmap(true)
                                    .dispatcher(Dispatchers.IO)
                                    .data(image).build()
                                imageLoader
                                    .execute(imageRequest)
                                val path =
                                    image?.let { img -> imageLoader.diskCache?.openSnapshot(img)?.data }

                                UiState.Success(
                                    data = currentDataState.copy(
                                        first = userProfile.calculateMaxDailyNutrition(userNutrition = userNutrition),
                                        second = currentDataState.second.copy(
                                            image = path?.toFile()
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