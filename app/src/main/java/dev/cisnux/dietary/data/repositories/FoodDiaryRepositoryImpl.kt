package dev.cisnux.dietary.data.repositories

import dev.cisnux.dietary.data.locals.BaseApiUrlLocalSource
import dev.cisnux.dietary.data.remotes.FoodDiaryRemoteSource
import dev.cisnux.dietary.data.remotes.bodyrequests.FoodDiaryBodyRequest
import dev.cisnux.dietary.data.remotes.bodyrequests.GetFoodDiaryBodyRequest
import dev.cisnux.dietary.domain.models.AddFoodDiary
import dev.cisnux.dietary.domain.models.Bound
import dev.cisnux.dietary.domain.models.Food
import dev.cisnux.dietary.domain.models.FoodDiary
import dev.cisnux.dietary.domain.models.FoodDiaryQuestion
import dev.cisnux.dietary.domain.models.FoodDiaryDetail
import dev.cisnux.dietary.domain.models.FoodDiaryReport
import dev.cisnux.dietary.domain.models.Option
import dev.cisnux.dietary.domain.models.PredictedFood
import dev.cisnux.dietary.domain.models.Question
import dev.cisnux.dietary.domain.models.Report
import dev.cisnux.dietary.domain.repositories.FoodRepository
import dev.cisnux.dietary.utils.dayDateMonthYear
import dev.cisnux.dietary.utils.hoursAndMinutes
import dev.cisnux.dietary.utils.FoodDiaryCategory
import dev.cisnux.dietary.utils.IMAGE_LOCATION
import dev.cisnux.dietary.utils.ReportCategory
import dev.cisnux.dietary.utils.UiState
import dev.cisnux.dietary.utils.convertISOToInstant
import dev.cisnux.dietary.utils.dateAndMonth
import dev.cisnux.dietary.utils.dayDateMonth
import dev.cisnux.dietary.utils.getCurrentDateTimeInISOFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.time.Instant
import kotlin.random.Random

class FoodDiaryRepositoryImpl @Inject constructor(
    private val foodDiaryRemoteSource: FoodDiaryRemoteSource,
    private val baseApiUrlLocalSource: BaseApiUrlLocalSource
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
    private val keywordSuggestions = listOf(
        "Kwetiau",
        "McDonald Burger",
        "Nasi Goreng",
        "Pempek",
        "Bakso",
        "Mie Ayam",
        "Mie Celor",
        "Capcay",
        "Nasi Padang",
        "Nasi Bakar",
        "Pizza",
        "Kebab"
    )
    private val foodDiaryDetail = MutableStateFlow(
        FoodDiaryDetail(
            foodDiaryId = "1",
            totalFoodCalories = 200.4512f,
            maxDailyBmrCalorie = 800.6798f,
            totalUserCaloriesToday = 500.7892f,
            status = "Boleh dimakan",
            feedbacks = listOf(
                "Terlalu berminyak",
                "Terlalu banyak gula",
                "Kurang protein",
            ),
            foods = listOf(
                Food(
                    id = "1",
                    name = "Nasi",
                    calorie = 50f,
                    protein = 8f,
                    fat = 2f,
                    carbohydrates = 4.3f,
                    sugar = 8.7f,
                    bound = Bound(
                        x = 404.0,
                        y = 75.0,
                        width = 411.0,
                        height = 308.0
                    ),
                    questions = listOf(
                    )
                ),
                Food(
                    id = "2",
                    name = "Ayam Bakar",
                    calorie = 50f,
                    protein = 6f,
                    fat = 10f,
                    carbohydrates = 8.3f,
                    sugar = null,
                    bound = Bound(
                        x = 404.0,
                        y = 75.0,
                        width = 411.0,
                        height = 308.0
                    ),
                    questions = listOf(
                    )
                ),
                Food(
                    id = "3",
                    name = "Tempe",
                    calorie = 5.8f,
                    protein = 9f,
                    fat = 1f,
                    carbohydrates = 8.3f,
                    sugar = 0f,
                    bound = Bound(
                        x = 404.0,
                        y = 75.0,
                        width = 411.0,
                        height = 308.0
                    ),
                    questions = listOf(
                    )
                ),
                Food(
                    id = "4",
                    name = "Sayur Kangkung",
                    calorie = 5.8f,
                    protein = 9f,
                    fat = 0.5f,
                    carbohydrates = 8.3f,
                    sugar = 0f,
                    questions = null,
                    bound = Bound(
                        x = 404.0,
                        y = 75.0,
                        width = 411.0,
                        height = 308.0
                    ),
                ),
            )
        )
    )

    private val anotherFoodDiaryDetail = FoodDiaryDetail(
        foodDiaryId = "1",
        totalFoodCalories = 200.4512f,
        maxDailyBmrCalorie = 800.6798f,
        totalUserCaloriesToday = 500.7892f,
        status = "Kurang disarankan",
        feedbacks = listOf(
            "Terlalu berminyak",
            "Terlalu banyak gula",
            "Kurang protein",
        ),
        foodPicture = "https://awsimages.detik.net.id/community/media/visual/2020/07/06/nasi-padang.jpeg?w=600&q=90",
        foods = listOf(
            Food(
                id = "1",
                name = "Nasi",
                calorie = 50f,
                protein = 8f,
                fat = 2f,
                carbohydrates = 4.3f,
                sugar = 8.7f,
                bound = Bound(
                    x = 404.0,
                    y = 75.0,
                    width = 411.0,
                    height = 308.0
                ),
            ),
            Food(
                id = "2",
                name = "Ayam Bakar",
                calorie = 50f,
                protein = 6f,
                fat = 10f,
                carbohydrates = 8.3f,
                sugar = null,
                bound = Bound(
                    x = 404.0,
                    y = 75.0,
                    width = 411.0,
                    height = 308.0
                ),
            ),
            Food(
                id = "3",
                name = "Tempe Goreng",
                calorie = 5.8f,
                protein = 9f,
                fat = 1f,
                carbohydrates = 8.3f,
                sugar = 0f,
                bound = Bound(
                    x = 404.0,
                    y = 75.0,
                    width = 411.0,
                    height = 308.0
                ),
            ),
            Food(
                id = "4",
                name = "Sayur Kangkung",
                calorie = 5.8f,
                protein = 9f,
                fat = 0.5f,
                carbohydrates = 8.3f,
                sugar = 0f,
                bound = Bound(
                    x = 404.0,
                    y = 75.0,
                    width = 411.0,
                    height = 308.0
                ),
            ),
        )
    )

    private val foodDiaryReportToday = Report(
        totalUserCaloriesToday = 500.7892f,
        maxDailyBmiCalorie = 800.6798f,
        foods = List(10) {
            FoodDiaryReport(
                id = it.toString(),
                title = "Warteg $it",
                totalFoodCalories = Random.nextDouble(80.0, 500.0).toFloat(),
                label = Instant.now().hoursAndMinutes()
            )
        }
    )

    private val foodDiaryReportWeek = Report(
        totalUserCaloriesToday = 500.7892f,
        maxDailyBmiCalorie = 800.6798f,
        foods = List(12) {
            FoodDiaryReport(
                id = it.toString(),
                title = "Warter $it",
                totalFoodCalories = Random.nextDouble(80.0, 500.0).toFloat(),
                label = Instant.now().dayDateMonth()
            )
        }
    )

    private val foodDiaryReportMonth = Report(
        totalUserCaloriesToday = 500.7892f,
        maxDailyBmiCalorie = 800.6798f,
        foods = List(25) {
            FoodDiaryReport(
                id = it.toString(),
                title = "Warter $it",
                totalFoodCalories = Random.nextDouble(80.0, 500.0).toFloat(),
                label = Instant.now().dateAndMonth()
            )
        }
    )
    override val baseUrl: Flow<String>
        get() = baseApiUrlLocalSource.baseApiUrl.flowOn(Dispatchers.IO)


    override fun getDiaryFoodsByDays(
        accessToken: String,
        userId: String,
        date: String,
        category: FoodDiaryCategory
    ): Flow<UiState<List<FoodDiary>>> = channelFlow {
        send(UiState.Loading)
        delay(1000L)
        foodDiaryRemoteSource.getFoodDiaries(
            accessToken = accessToken,
            getFoodDiaryBodyRequest = GetFoodDiaryBodyRequest(
                userId = userId,
                date = date,
                category = category.value,
            )
        ).fold(
            ifLeft = { exception ->
                send(UiState.Error(exception))
            },
            ifRight = { foodDiaries ->
                send(UiState.Success(foodDiaries.map { foodDiary ->
                    FoodDiary(
                        id = foodDiary.id,
                        title = foodDiary.title,
                        date = convertISOToInstant(foodDiary.addedAt).dayDateMonthYear(),
                        time = convertISOToInstant(foodDiary.addedAt).hoursAndMinutes(),
                        foodPictureUrl = "$IMAGE_LOCATION/${foodDiary.filePath}",
                        totalFoodCalories = foodDiary.totalFoodCalories
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

    override fun getKeywordSuggestionsByQuery(query: String): Flow<UiState<List<String>>> = flow {
        emit(UiState.Loading)
//        emit(UiState.Error(Failure.BadRequestFailure("bad request")))
        emit(UiState.Success(keywordSuggestions.filter { it.contains(query.lowercase()) }))
    }.flowOn(Dispatchers.IO)
        .distinctUntilChanged()

    override fun addFoodDiary(
        accessToken: String,
        userId: String,
        addFoodDiary: AddFoodDiary
    ): Flow<UiState<FoodDiaryDetail>> =
        flow {
            emit(UiState.Loading)
            delay(1000L)
            val addedAt = getCurrentDateTimeInISOFormat()
            foodDiaryRemoteSource.addFoodDiary(
                accessToken = accessToken,
                foodDiary = FoodDiaryBodyRequest(
                    title = addFoodDiary.title,
                    category = addFoodDiary.category,
                    foodPicture = addFoodDiary.foodPicture,
                    userAccountId = userId,
                    addedAt = addedAt
                )
            ).fold(
                ifLeft = { exception ->
                    emit(UiState.Error(exception))
                },
                ifRight = { addedFoodDiary ->
                    emit(
                        UiState.Success(
                            foodDiaryDetail.value
                        )
                    )
                }
            )
        }.flowOn(Dispatchers.IO)
            .distinctUntilChanged()

    override fun predictFood(
        accessToken: String,
        foodPicture: File
    ): Flow<UiState<List<PredictedFood>>> =
        flow {
            emit(UiState.Loading)
            foodDiaryRemoteSource.predictFoods(accessToken, foodPicture).fold(
                ifLeft = { exception ->
                    emit(UiState.Error(exception))
                },
                ifRight = {
                    emit(UiState.Success(it.foods.map { predictedFoodResponse ->
                        val serving = predictedFoodResponse.foodDetail.serving.first { servingResponse ->
                            servingResponse.metricServingAmount == 100
                        }
                        PredictedFood(
                            id = predictedFoodResponse.foodDetail.id,
                            name = predictedFoodResponse.foodDetail.name,
                            bound = Bound(
                                x = predictedFoodResponse.bounds.bound.x,
                                y = predictedFoodResponse.bounds.bound.y,
                                height = predictedFoodResponse.bounds.bound.height,
                                width = predictedFoodResponse.bounds.bound.width,
                            ),
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
                            feedbacks = predictedFoodResponse.foodDetail.questions.filter { questionResponse -> questionResponse.type == "information" }
                                .map { questionResponse -> questionResponse.question },
                            )
                    }))
                }
            )
        }.flowOn(Dispatchers.IO)
            .distinctUntilChanged()

    override fun updateFoodDiaryBaseOnAnsweredQuestion(foodDiaryQuestion: FoodDiaryQuestion): Flow<UiState<FoodDiaryDetail>> =
        flow {
            emit(UiState.Loading)
            delay(1000L)
            emit(UiState.Success(foodDiaryDetail.value.copy(
                status = "Kurang disarankan",
                feedbacks = listOf(
                    "Terlalu berminyak",
                    "Terlalu banyak gula",
                    "Kurang protein",
                ),
                foods = foodDiaryDetail.value.foods.map {
                    it.copy(
                        questions = null
                    )
                }
            )))
            //        emit(UiState.Error(Failure.BadRequestFailure("bad request")))
//                                emit(UiState.Error(error = Failure.ConnectionFailure("No internet access")))
        }.flowOn(Dispatchers.IO)
            .distinctUntilChanged()

    override fun deleteFoodDiaryById(foodDiaryId: String): Flow<UiState<Nothing>> = flow {
        emit(UiState.Loading)
        delay(1000L)
        //        emit(UiState.Error(Failure.BadRequestFailure("bad request")))
//        emit(UiState.Error(error = Failure.ConnectionFailure("No internet access")))
        emit(UiState.Success())
    }.flowOn(Dispatchers.IO)
        .distinctUntilChanged()

    override fun getFoodDiaryDetailById(foodDiaryId: String): Flow<UiState<FoodDiaryDetail>> =
        flow {
            emit(UiState.Loading)
            delay(1000L)
            emit(UiState.Success(anotherFoodDiaryDetail))
            //        emit(UiState.Error(Failure.BadRequestFailure("bad request")))
//                            emit(UiState.Error(error = Failure.ConnectionFailure("No internet access")))
        }.flowOn(Dispatchers.IO)
            .distinctUntilChanged()

    override fun getFoodDiaryReports(category: ReportCategory): Flow<UiState<Report>> =
        flow {
            emit(UiState.Loading)
            delay(1000L)
            //        emit(UiState.Error(Failure.BadRequestFailure("bad request")))
//        emit(UiState.Error(error = Failure.ConnectionFailure("No internet access")))
            emit(
                UiState.Success(
                    when (category) {
                        ReportCategory.TODAY -> foodDiaryReportToday
                        ReportCategory.THIS_WEEK -> foodDiaryReportWeek
                        else -> foodDiaryReportMonth
                    }
                )
            )
        }.flowOn(Dispatchers.IO)
            .distinctUntilChanged()

    override suspend fun updateBaseUrlApi(baseUrl: String) = withContext(Dispatchers.IO) {
        baseApiUrlLocalSource.updateBaseApiUrl(baseApiUrl = baseUrl)
    }
}