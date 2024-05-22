package org.cisnux.mydietary.data.repositories

import org.cisnux.mydietary.data.locals.BaseApiUrlLocalSource
import org.cisnux.mydietary.data.remotes.FoodDiaryRemoteSource
import org.cisnux.mydietary.data.remotes.bodyrequests.FoodDiaryBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.GetFoodDiaryParams
import org.cisnux.mydietary.domain.models.AddFoodDiary
import org.cisnux.mydietary.domain.models.FoodDiary
import org.cisnux.mydietary.domain.models.FoodDiaryDetail
import org.cisnux.mydietary.domain.models.FoodDiaryReport
import org.cisnux.mydietary.domain.models.FoodNutrition
import org.cisnux.mydietary.domain.models.Option
import org.cisnux.mydietary.domain.models.Food
import org.cisnux.mydietary.domain.models.Question
import org.cisnux.mydietary.domain.repositories.FoodRepository
import org.cisnux.mydietary.utils.fromMillisToDayDateMonthYear
import org.cisnux.mydietary.utils.fromMillisToHoursAndMinutes
import org.cisnux.mydietary.utils.IMAGE_LOCATION
import org.cisnux.mydietary.utils.ReportCategory
import org.cisnux.mydietary.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlinx.coroutines.withContext
import org.cisnux.mydietary.data.remotes.bodyrequests.ReportBodyRequest
import org.cisnux.mydietary.data.remotes.responses.MonthlyReportResponse
import org.cisnux.mydietary.data.remotes.responses.WeeklyReportResponse
import org.cisnux.mydietary.domain.models.Keyword
import org.cisnux.mydietary.utils.FoodDiaryCategory
import org.cisnux.mydietary.utils.fromDateMonthYearToDateAndMonth
import org.cisnux.mydietary.utils.fromDateMonthYearToDay
import org.cisnux.mydietary.utils.asInstant
import org.cisnux.mydietary.utils.currentLocalDateTime
import org.cisnux.mydietary.utils.fromDateMonthYearToDayDateMonth
import java.io.File

class FoodDiaryRepositoryImpl @Inject constructor(
    private val foodDiaryRemoteSource: FoodDiaryRemoteSource,
    private val baseApiUrlLocalSource: BaseApiUrlLocalSource,
) : FoodRepository {

    override val baseUrl: Flow<String>
        get() = baseApiUrlLocalSource.baseApiUrl.flowOn(Dispatchers.IO)


    override fun getFoodDiaries(
        accessToken: String,
        userId: String,
        date: String?,
        category: FoodDiaryCategory?,
        query: String?
    ): Flow<UiState<List<FoodDiary>>> = flow {
        emit(UiState.Loading)
        foodDiaryRemoteSource.getFoodDiaries(
            accessToken = accessToken,
            getFoodDiaryParams = GetFoodDiaryParams(
                userId = userId,
                date = date,
                category = category?.value,
                query = query
            )
        ).fold(
            ifLeft = { exception ->
                emit(UiState.Error(exception))
            },
            ifRight = { foodDiaries ->
                emit(
                    UiState.Success(foodDiaries
                        .map { foodDiary ->
                            FoodDiary(
                                id = foodDiary.id,
                                title = foodDiary.title,
                                date = foodDiary.addedAt.asInstant.fromMillisToDayDateMonthYear,
                                time = foodDiary.addedAt.asInstant.fromMillisToHoursAndMinutes,
                                foodPictureUrl = "$IMAGE_LOCATION/${foodDiary.filePath}",
                                totalFoodCalories = foodDiary.totalCalories
                            )
                        })
                )
            }
        )
    }.distinctUntilChanged()
        .flowOn(Dispatchers.IO)

    override fun getFoodDiaries(
        accessToken: String,
        userId: String,
        date: String
    ): Flow<UiState<List<FoodDiary>>> = flow {
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
                        date = foodDiary.addedAt.asInstant.fromMillisToDayDateMonthYear,
                        time = foodDiary.addedAt.asInstant.fromMillisToHoursAndMinutes,
                        foodPictureUrl = "$IMAGE_LOCATION/${foodDiary.filePath}",
                        totalFoodCalories = foodDiary.totalCalories
                    )
                }))
            }
        )
    }.distinctUntilChanged()
        .flowOn(Dispatchers.IO)

    override fun getKeywordSuggestions(
        accessToken: String,
        userId: String,
        query: String,
    ): Flow<UiState<List<Keyword>>> = flow {
        emit(UiState.Loading)
        foodDiaryRemoteSource.getKeywordSuggestions(
            accessToken = accessToken,
            getFoodDiaryParams = GetFoodDiaryParams(userId = userId, query = query)
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
            val addedAt = currentLocalDateTime
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

    override fun predictFoods(
        accessToken: String,
        foodPicture: File,
    ): Flow<UiState<FoodNutrition>> =
        flow {
            emit(UiState.Loading)
            foodDiaryRemoteSource.predictFoods(accessToken, foodPicture).fold(
                ifLeft = { exception -> emit(UiState.Error(exception)) },
                ifRight = {
                    emit(UiState.Success(data = FoodNutrition(
                        image = "$IMAGE_LOCATION/${it.imagePath}",
                        totalFat = it.totalFat,
                        totalProtein = it.totalProtein,
                        totalCalories = it.totalCalories,
                        totalCarbohydrate = it.totalCarbohydrate,
                        foods = it.foods.map { predictedFoodResponse ->
                            val serving =
                                predictedFoodResponse.foodDetail.serving.first { servingResponse ->
                                    servingResponse.metricServingAmount.toInt() == 100
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
                }
            )
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
                                        servingResponse.metricServingAmount.toInt() == 100
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
                reportBodyRequest = ReportBodyRequest(
                    userId = userId,
                    reportType = category.reportType
                )
            ).fold(
                ifLeft = { exception -> emit(UiState.Error(exception)) },
                ifRight = {
                    val foodDiaries = it.map { reportResponse ->
                        when (reportResponse) {
                            is MonthlyReportResponse -> {
                                FoodDiaryReport(
                                    averageCarbohydrate = reportResponse.averageCarbohydrate,
                                    averageCalories = reportResponse.averageCalories,
                                    averageFat = reportResponse.averageFat,
                                    averageProtein = reportResponse.averageProtein,
                                    label = reportResponse.week.toString(),
                                    description = "${reportResponse.startDate.fromDateMonthYearToDateAndMonth} - ${reportResponse.endDate.fromDateMonthYearToDateAndMonth}",
                                )
                            }

                            is WeeklyReportResponse -> FoodDiaryReport(
                                averageCarbohydrate = reportResponse.averageCarbohydrate,
                                averageCalories = reportResponse.averageCalories,
                                averageFat = reportResponse.averageFat,
                                averageProtein = reportResponse.averageProtein,
                                label = reportResponse.date.fromDateMonthYearToDay,
                                description = reportResponse.date.fromDateMonthYearToDayDateMonth,
                                date = reportResponse.date
                            )
                        }
                    }
                    emit(
                        UiState.Success(
                            foodDiaries
                        )
                    )
                }
            )
        }.flowOn(Dispatchers.IO)
            .distinctUntilChanged()

    override suspend fun updateBaseUrlApi(baseUrl: String) = withContext(Dispatchers.IO) {
        baseApiUrlLocalSource.updateBaseApiUrl(baseApiUrl = baseUrl)
    }
}