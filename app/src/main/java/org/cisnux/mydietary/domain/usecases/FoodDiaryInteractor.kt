package org.cisnux.mydietary.domain.usecases

import android.content.Context
import androidx.core.graphics.drawable.toBitmapOrNull
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.request.SuccessResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import org.cisnux.mydietary.domain.models.AddFoodDiary
import org.cisnux.mydietary.domain.models.FoodDiary
import org.cisnux.mydietary.domain.models.FoodDiaryDetail
import org.cisnux.mydietary.domain.models.FoodNutrition
import org.cisnux.mydietary.domain.repositories.FoodRepository
import org.cisnux.mydietary.commons.utils.FoodDiaryCategory
import org.cisnux.mydietary.commons.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import org.cisnux.mydietary.domain.models.Keyword
import java.io.File
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class FoodDiaryInteractor @Inject constructor(
    private val foodRepository: FoodRepository,
    private val authenticationUseCase: AuthenticationUseCase,
    @ApplicationContext private val context: Context
) : FoodDiaryUseCase {
    override fun getFoodDiariesByDaysAndCategory(
        date: String, category: FoodDiaryCategory, scope: CoroutineScope
    ): Flow<UiState<List<FoodDiary>>> =
        authenticationUseCase.getAccessTokenAndUserId(scope = scope)
            .filterNotNull()
            .flatMapLatest {
                foodRepository.getFoodDiaries(
                    userId = it.first, accessToken = it.second, date = date, category = category
                ).flowOn(Dispatchers.IO).stateIn(
                    scope = scope,
                    started = SharingStarted.Lazily,
                    initialValue = UiState.Initialize
                )
            }
            .flowOn(Dispatchers.IO)
            .stateIn(
                scope = scope,
                started = SharingStarted.Lazily,
                initialValue = UiState.Initialize
            )

    override fun getFoodDiariesByDaysForWidget(
        date: String,
        scope: CoroutineScope
    ): Flow<UiState<List<FoodDiary>>> =
        authenticationUseCase.getAccessTokenAndUserId(scope = scope)
            .filterNotNull()
            .flatMapLatest {
                foodRepository.getFoodDiaries(
                    userId = it.first, accessToken = it.second, date = date
                ).map { uiState ->
                    if (uiState is UiState.Success)
                        uiState.copy(data = uiState.data?.map { foodDiary ->
                            val imageLoader = ImageLoader(context)
                            val request = ImageRequest.Builder(context)
                                .size(100)
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
                    .shareIn(scope = scope, SharingStarted.WhileSubscribed(replayExpirationMillis = 1000L))
            }
            .flowOn(Dispatchers.IO)
            .shareIn(scope = scope, SharingStarted.WhileSubscribed(replayExpirationMillis = 1000L))

    override fun getFoodDiariesByQuery(
        query: String,
        scope: CoroutineScope
    ): Flow<UiState<List<FoodDiary>>> =
        authenticationUseCase.getAccessTokenAndUserId(scope = scope)
            .filterNotNull()
            .flatMapLatest {
                foodRepository.getFoodDiaries(
                    userId = it.first,
                    accessToken = it.second,
                    query = query
                ).flowOn(Dispatchers.IO)
                    .stateIn(
                        scope = scope,
                        started = SharingStarted.Lazily,
                        initialValue = UiState.Initialize
                    )
            }.flowOn(Dispatchers.IO)
            .stateIn(
                scope = scope,
                started = SharingStarted.Lazily,
                initialValue = UiState.Initialize
            )

    override fun getSuggestionKeywordsByQuery(
        query: String,
        scope: CoroutineScope
    ): Flow<UiState<List<Keyword>>> =
        authenticationUseCase.getAccessTokenAndUserId(scope = scope)
            .filterNotNull()
            .flatMapLatest {
                foodRepository.getSuggestionKeywords(
                    userId = it.first, accessToken = it.second, query = query
                )
                    .flowOn(Dispatchers.IO)
                    .stateIn(
                        scope = scope,
                        started = SharingStarted.Lazily,
                        initialValue = UiState.Initialize
                    )
            }.flowOn(Dispatchers.IO)
            .stateIn(
                scope = scope,
                started = SharingStarted.Lazily,
                initialValue = UiState.Initialize
            )

    override fun addFoodDiary(
        addFoodDiary: AddFoodDiary,
        scope: CoroutineScope
    ): Flow<UiState<String>> =
        authenticationUseCase
            .getAccessTokenAndUserId(scope = scope)
            .filterNotNull()
            .distinctUntilChanged()
            .flatMapLatest {
                foodRepository.addFoodDiary(
                    userId = it.first, accessToken = it.second, addFoodDiary = addFoodDiary
                ).stateIn(
                    scope = scope,
                    started = SharingStarted.Lazily,
                    initialValue = UiState.Initialize
                )
            }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
            .stateIn(
                scope = scope,
                started = SharingStarted.Lazily,
                initialValue = UiState.Initialize
            )

    @OptIn(ExperimentalCoilApi::class)
    override fun getFoodDiaryDetailById(
        foodDiaryId: String,
        scope: CoroutineScope
    ): Flow<UiState<FoodDiaryDetail>> =
        authenticationUseCase.getAccessToken(scope = scope)
            .filterNotNull()
            .filter { it.isNotBlank() }
            .flatMapLatest { accessToken ->
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
                    .flowOn(Dispatchers.IO)
                    .stateIn(
                        scope = scope,
                        started = SharingStarted.Lazily,
                        initialValue = UiState.Initialize
                    )
            }
            .flowOn(Dispatchers.IO)
            .stateIn(
                scope = scope,
                started = SharingStarted.Lazily,
                initialValue = UiState.Initialize
            )

    override fun deleteFoodDiaryById(
        foodDiaryId: String,
        scope: CoroutineScope
    ): Flow<UiState<Nothing>> =
        authenticationUseCase.getAccessToken(scope = scope)
            .filterNotNull()
            .filter { it.isNotBlank() }
            .flatMapLatest { accessToken ->
                foodRepository.deleteFoodDiaryById(accessToken, foodDiaryId)
                    .stateIn(
                        scope = scope,
                        started = SharingStarted.Lazily,
                        initialValue = UiState.Initialize
                    )
            }
            .flowOn(Dispatchers.IO)
            .stateIn(
                scope = scope,
                started = SharingStarted.Lazily,
                initialValue = UiState.Initialize
            )

    @OptIn(ExperimentalCoilApi::class)
    override fun predictFoods(
        foodPicture: File,
        scope: CoroutineScope
    ): Flow<UiState<FoodNutrition>> =
        authenticationUseCase.getAccessToken(scope = scope)
            .filterNotNull()
            .filter { it.isNotBlank() }
            .flatMapLatest { accessToken ->
                foodRepository.predictFoods(
                    accessToken = accessToken,
                    foodPicture = foodPicture
                )
                    .map { uiState ->
                        if (uiState is UiState.Success) {
                            val currentDataState = uiState.data!!

                            val image = currentDataState.image as String?
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
                                    image = path?.toFile()
                                )
                            )
                        } else uiState
                    }.flowOn(Dispatchers.IO)
                    .stateIn(
                        scope = scope,
                        started = SharingStarted.Lazily,
                        initialValue = UiState.Initialize
                    )
            }
            .flowOn(Dispatchers.IO)
            .stateIn(
                scope = scope,
                started = SharingStarted.Lazily,
                initialValue = UiState.Initialize
            )

    override val baseUrl: Flow<String>
        get() = foodRepository.baseUrl

    override suspend fun updateBaseUrlApi(baseUrl: String) =
        foodRepository.updateBaseUrlApi(baseUrl = baseUrl)
}