package org.cisnux.mydietary.data.repositories

import android.content.Context
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.request.ImageRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import org.cisnux.mydietary.data.locals.BaseApiUrlLocalSource
import org.cisnux.mydietary.data.remotes.FoodDiaryRemoteSource
import org.cisnux.mydietary.data.remotes.UserProfileRemoteSource
import org.cisnux.mydietary.data.remotes.bodyrequests.FoodDiaryBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.GetFoodDiaryParams
import org.cisnux.mydietary.domain.models.AddFoodDiary
import org.cisnux.mydietary.domain.models.FoodDiary
import org.cisnux.mydietary.domain.models.FoodDiaryDetail
import org.cisnux.mydietary.domain.models.FoodDiaryReport
import org.cisnux.mydietary.domain.models.FoodNutrition
import org.cisnux.mydietary.domain.models.UserNutrition
import org.cisnux.mydietary.domain.models.Option
import org.cisnux.mydietary.domain.models.Food
import org.cisnux.mydietary.domain.models.Question
import org.cisnux.mydietary.domain.repositories.FoodRepository
import org.cisnux.mydietary.utils.dayDateMonthYear
import org.cisnux.mydietary.utils.hoursAndMinutes
import org.cisnux.mydietary.utils.FoodDiaryCategory
import org.cisnux.mydietary.utils.IMAGE_LOCATION
import org.cisnux.mydietary.utils.ReportCategory
import org.cisnux.mydietary.utils.UiState
import org.cisnux.mydietary.utils.convertISOToInstant
import org.cisnux.mydietary.utils.getCurrentDateTimeInISOFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import org.cisnux.mydietary.domain.models.Keyword
import org.cisnux.mydietary.utils.asDateAndMonth
import java.io.File

class FoodDiaryRepositoryImpl @Inject constructor(
    private val foodDiaryRemoteSource: FoodDiaryRemoteSource,
    private val baseApiUrlLocalSource: BaseApiUrlLocalSource,
    private val userProfileRemoteSource: UserProfileRemoteSource,
    @ApplicationContext private val context: Context,
) : FoodRepository {
    private val foodsBreakfastDiary = List(10) {
        FoodDiary(
            id = it.toString(),
            title = "Mie Ayam",
            date = "Senin, 21 Feb",
            time = "18:20",
            foodPictureUrl = "https://allofresh.id/blog/wp-content/uploads/2023/08/cara-membuat-mie-ayam-4.jpg",
            totalFoodCalories = 500f
        )
    }

    override val baseUrl: Flow<String>
        get() = baseApiUrlLocalSource.baseApiUrl.flowOn(Dispatchers.IO)


    override fun getDiaryFoodsByDate(
        accessToken: String,
        userId: String,
        date: String,
        category: FoodDiaryCategory
    ): Flow<UiState<List<FoodDiary>>> = flow{
        emit(UiState.Loading)
        foodDiaryRemoteSource.getFoodDiaries(
            accessToken = accessToken,
            getFoodDiaryParams = GetFoodDiaryParams(
                userId = userId,
                date = date,
                category = category.value,
            )
        ).fold(
            ifLeft = { exception ->
                emit(UiState.Error(exception))
            },
            ifRight = { foodDiaries ->
                emit(UiState.Success(foodDiaries.map { foodDiary ->
                    FoodDiary(
                        id = foodDiary.id,
                        title = foodDiary.title,
                        date = convertISOToInstant(foodDiary.addedAt).dayDateMonthYear(),
                        time = convertISOToInstant(foodDiary.addedAt).hoursAndMinutes(),
                        foodPictureUrl = "$IMAGE_LOCATION/${foodDiary.filePath}",
                        totalFoodCalories = foodDiary.totalCalories
                    )
                }))
            }
        )
    }.distinctUntilChanged()
        .flowOn(Dispatchers.IO)

    override fun getDiaryFoodsByDate(
        accessToken: String,
        userId: String,
        date: String
    ): Flow<UiState<List<FoodDiary>>> = flow{
        emit(UiState.Loading)
        foodDiaryRemoteSource.getFoodDiaries(
            accessToken = accessToken,
            getFoodDiaryParams = GetFoodDiaryParams(
                userId = userId,
                date = date,
            )
        ).fold(
            ifLeft = { exception ->
                emit(UiState.Error(exception))
            },
            ifRight = { foodDiaries ->
                emit(UiState.Success(foodDiaries.map { foodDiary ->
                    FoodDiary(
                        id = foodDiary.id,
                        title = foodDiary.title,
                        date = convertISOToInstant(foodDiary.addedAt).dayDateMonthYear(),
                        time = convertISOToInstant(foodDiary.addedAt).hoursAndMinutes(),
                        foodPictureUrl = "$IMAGE_LOCATION/${foodDiary.filePath}",
                        totalFoodCalories = foodDiary.totalCalories
                    )
                }))
            }
        )
    }.distinctUntilChanged()
    .flowOn(Dispatchers.IO)

    override fun getDiaryFoodsByQuery(query: String): Flow<UiState<List<FoodDiary>>> = flow {
        emit(UiState.Loading)
        delay(1000L)
//        emit(UiState.Error(Failure.BadRequestFailure("bad request")))
//        emit(UiState.Success(listOf()))
        emit(UiState.Success(foodsBreakfastDiary))
    }

    override fun getKeywordSuggestions(
        accessToken: String,
        userId: String
    ): Flow<UiState<List<Keyword>>> = flow {
        emit(UiState.Loading)
        foodDiaryRemoteSource.getKeywordSuggestions(
            accessToken = accessToken,
            getFoodDiaryParams = GetFoodDiaryParams(userId = userId)
        )
            .fold(
                ifLeft = { exception ->
                    emit(UiState.Error(exception))
                },
                ifRight = {
                    emit(UiState.Success(it.map { keywordResponse ->
                        Keyword(
                            id = keywordResponse.id,
                            text = keywordResponse.title
                        )
                    }))
                }
            )
    }.flowOn(Dispatchers.IO)
        .distinctUntilChanged()

    override fun addFoodDiary(
        accessToken: String,
        userId: String,
        addFoodDiary: AddFoodDiary
    ): Flow<UiState<String>> =
        flow {
            emit(UiState.Loading)
            val addedAt = getCurrentDateTimeInISOFormat()
            foodDiaryRemoteSource.addFoodDiary(
                accessToken = accessToken,
                foodDiary = FoodDiaryBodyRequest(
                    title = addFoodDiary.title,
                    category = addFoodDiary.category,
                    foodPicture = addFoodDiary.foodPicture,
                    userAccountId = userId,
                    addedAt = addedAt,
                    foodIds = addFoodDiary.foodIds,
                    totalProtein = addFoodDiary.totalProtein,
                    totalFat = addFoodDiary.totalFat,
                    totalCarbohydrate = addFoodDiary.totalCarbohydrate,
                    totalCalories = addFoodDiary.totalCalories,
                    feedback = addFoodDiary.feedback
                )
            ).fold(
                ifLeft = { exception ->
                    emit(UiState.Error(exception))
                },
                ifRight = { foodId ->
                    emit(
                        UiState.Success(
                            foodId
                        )
                    )
                }
            )
        }.flowOn(Dispatchers.IO)
            .distinctUntilChanged()

    @OptIn(ExperimentalCoilApi::class)
    override fun predictFood(
        userId: String,
        accessToken: String,
        foodPicture: File,
        date: String
    ): Flow<UiState<Pair<UserNutrition, FoodNutrition>>> =
        channelFlow {
            trySend(UiState.Loading)
            withContext(Dispatchers.IO) {
                val deferredPredictFoods = async {
                    foodDiaryRemoteSource.predictFoods(accessToken, foodPicture)
                }
                val deferredDailyNutrients = async {
                    userProfileRemoteSource.getDailyNutrients(
                        accessToken = accessToken,
                        userId = userId,
                        date = date
                    )
                }
                Pair(deferredPredictFoods.await(), deferredDailyNutrients.await()).run {
                    first.isLeft {
                        trySend(UiState.Error(it))
                        true
                    }
                    second.isLeft {
                        trySend(UiState.Error(it))
                        true
                    }
                    val predictedResponse = first.getOrNull()
                    val nutrientResponse = second.getOrNull()
                    if (predictedResponse != null && nutrientResponse != null) {
                        val image = "$IMAGE_LOCATION/${predictedResponse.imagePath}"
                        val imageLoader = ImageLoader(context)
                        val imageRequest = ImageRequest.Builder(context)
                            .allowConversionToBitmap(true)
                            .dispatcher(Dispatchers.IO)
                            .data(image).build()
                        imageLoader
                            .execute(imageRequest)
                        val path =
                            imageLoader.diskCache?.openSnapshot(image)?.data
                        trySend(
                            UiState.Success(
                                data = Pair(
                                    first = UserNutrition(
                                        totalCaloriesToday = nutrientResponse.calories,
                                        totalFatToday = nutrientResponse.fat,
                                        totalCarbohydrateToday = nutrientResponse.carbohydrate,
                                        totalProteinToday = nutrientResponse.protein
                                    ),
                                    second = FoodNutrition(
                                        image = path?.toFile(),
                                        totalFat = predictedResponse.totalFat,
                                        totalProtein = predictedResponse.totalProtein,
                                        totalCalories = predictedResponse.totalCalories,
                                        totalCarbohydrate = predictedResponse.totalCarbohydrate,
                                        foods = predictedResponse.foods.map { predictedFoodResponse ->
                                            val serving =
                                                predictedFoodResponse.foodDetail.serving.first { servingResponse ->
                                                    servingResponse.metricServingAmount == 100
                                                }
                                            Food(
                                                id = predictedFoodResponse.foodDetail.id,
                                                name = predictedFoodResponse.foodDetail.name,
                                                questions = predictedFoodResponse.foodDetail.questions.filter { questionResponse -> questionResponse.type == "food" }
                                                    .map { questionResponse ->
                                                        Question(
                                                            id = questionResponse.id,
                                                            question = questionResponse.question,
                                                            options = questionResponse.options.map { optionResponse ->
                                                                Option(
                                                                    id = optionResponse.id,
                                                                    answer = optionResponse.answer,
                                                                    reference = optionResponse.reference
                                                                )
                                                            })
                                                    },
                                                calories = serving.calories,
                                                carbohydrates = serving.carbohydrate,
                                                protein = serving.protein,
                                                fat = serving.fat,
                                                sugar = serving.sugar,
                                                feedback = predictedFoodResponse.foodDetail.questions.filter { questionResponse -> questionResponse.type == "information" }
                                                    .map { questionResponse -> questionResponse.question },
                                            )
                                        }
                                    )
                                )
                            )
                        )
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
            .distinctUntilChanged()


    override fun deleteFoodDiaryById(
        accessToken: String,
        foodDiaryId: String
    ): Flow<UiState<Nothing>> = flow {
        emit(UiState.Loading)
        foodDiaryRemoteSource.deleteFoodDiaryById(accessToken, foodDiaryId).fold(
            ifLeft = { exception ->
                emit(UiState.Error(exception))
            },
            ifRight = { emit(UiState.Success(null)) }
        )
    }.flowOn(Dispatchers.IO)
        .distinctUntilChanged()

    override fun getFoodDiaryDetailById(
        accessToken: String,
        foodDiaryId: String
    ): Flow<UiState<FoodDiaryDetail>> =
        flow {
            emit(UiState.Loading)
            foodDiaryRemoteSource.getFoodDiaryById(accessToken, foodDiaryId).fold(
                ifLeft = {
                    emit(UiState.Error(it))
                },
                ifRight = {
                    emit(UiState.Success(FoodDiaryDetail(id = it.id,
                        title = it.title,
                        feedback = it.feedback ?: listOf(),
                        status = it.status,
                        foodNutrition = FoodNutrition(
                            image = "$IMAGE_LOCATION/${it.filePath}",
                            totalProtein = it.totalProtein,
                            totalCalories = it.totalCalories,
                            totalCarbohydrate = it.totalCarbohydrate,
                            totalFat = it.totalFat,
                            foods = it.foods.map { predictedFoodDetailResponse ->
                                val serving =
                                    predictedFoodDetailResponse.serving.first { servingResponse ->
                                        servingResponse.metricServingAmount == 100
                                    }
                                Food(
                                    id = predictedFoodDetailResponse.id,
                                    name = predictedFoodDetailResponse.name,
                                    questions = predictedFoodDetailResponse.questions.filter { questionResponse -> questionResponse.type == "food" }
                                        .map { questionResponse ->
                                            Question(
                                                id = questionResponse.id,
                                                question = questionResponse.question,
                                                options = questionResponse.options.map { optionResponse ->
                                                    Option(
                                                        id = optionResponse.id,
                                                        answer = optionResponse.answer,
                                                        reference = optionResponse.reference
                                                    )
                                                })
                                        },
                                    calories = serving.calories,
                                    carbohydrates = serving.carbohydrate,
                                    protein = serving.protein,
                                    fat = serving.fat,
                                    sugar = serving.sugar,
                                    feedback = predictedFoodDetailResponse.questions.filter { questionResponse -> questionResponse.type == "information" }
                                        .map { questionResponse -> questionResponse.question },
                                )
                            }
                        ))))
                }
            )
        }.flowOn(Dispatchers.IO)
            .distinctUntilChanged()

    override fun getFoodDiaryReports(
        accessToken: String,
        userId: String,
        category: ReportCategory
    ): Flow<UiState<List<FoodDiaryReport>>> =
        flow {
            emit(UiState.Loading)
            foodDiaryRemoteSource.getFoodDiaryReports(
                accessToken = accessToken,
                userId = userId,
                category = category.name
            ).fold(
                ifLeft = { exception -> emit(UiState.Error(exception)) },
                ifRight = {
                    emit(UiState.Success(it.map { reportResponse ->
                        FoodDiaryReport(
                            averageCarbohydrate = reportResponse.averageCarbohydrate,
                            averageCalories = reportResponse.averageCalories,
                            averageFat = reportResponse.averageFat,
                            averageProtein = reportResponse.averageProtein,
                            label = reportResponse.week.toString(),
                            description = "(${reportResponse.startDate.asDateAndMonth} - ${reportResponse.endDate.asDateAndMonth})"
                        )
                    }))
                }
            )
        }.flowOn(Dispatchers.IO)
            .distinctUntilChanged()

    override suspend fun updateBaseUrlApi(baseUrl: String) = withContext(Dispatchers.IO) {
        baseApiUrlLocalSource.updateBaseApiUrl(baseApiUrl = baseUrl)
    }
}