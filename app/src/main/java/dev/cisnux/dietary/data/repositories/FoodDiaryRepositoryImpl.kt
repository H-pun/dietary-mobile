package dev.cisnux.dietary.data.repositories

import android.util.Log
import dev.cisnux.dietary.data.locals.UserAccountLocalSource
import dev.cisnux.dietary.data.remotes.FoodDiaryRemoteSource
import dev.cisnux.dietary.data.remotes.bodyrequests.FoodDiaryBodyRequest
import dev.cisnux.dietary.data.remotes.bodyrequests.GetFoodDiaryBodyRequest
import dev.cisnux.dietary.domain.models.AddFoodDiary
import dev.cisnux.dietary.domain.models.Food
import dev.cisnux.dietary.domain.models.FoodDiary
import dev.cisnux.dietary.domain.models.FoodDiaryQuestion
import dev.cisnux.dietary.domain.models.FoodDiaryDetail
import dev.cisnux.dietary.domain.models.FoodDiaryReport
import dev.cisnux.dietary.domain.models.Question
import dev.cisnux.dietary.domain.models.Report
import dev.cisnux.dietary.domain.repositories.FoodRepository
import dev.cisnux.dietary.utils.DIETARY_API
import dev.cisnux.dietary.utils.withFullDateFormat
import dev.cisnux.dietary.utils.withTimeFormat
import dev.cisnux.dietary.utils.FoodDiaryCategory
import dev.cisnux.dietary.utils.QuestionType
import dev.cisnux.dietary.utils.ReportCategory
import dev.cisnux.dietary.utils.UiState
import dev.cisnux.dietary.utils.convertISOToMillis
import dev.cisnux.dietary.utils.getCurrentDateTimeInISOFormat
import dev.cisnux.dietary.utils.withShortDateFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.toLocalTime
import java.time.Instant
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class FoodDiaryRepositoryImpl @Inject constructor(
    private val foodDiaryRemoteSource: FoodDiaryRemoteSource,
    private val userAccountLocalSource: UserAccountLocalSource
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
            feedback = null,
            foods = listOf(
                Food(
                    id = "1",
                    name = "Nasi",
                    calorie = 50f,
                    protein = 8f,
                    fat = 2f,
                    carbohydrates = 4.3f,
                    sugar = 8.7f,
                    questions = listOf(
                        Question(
                            id = "1",
                            question = "Apakah ini nasi putih?",
                            choices = listOf("Iya", "Tidak")
                        ),
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
                    questions = listOf(
                        Question(
                            id = "1",
                            question = "Apakah digoreng?",
                            choices = listOf("Ya", "Tidak")
                        ),
                        Question(
                            id = "2",
                            question = "Berapa kandungan gula dalam makanan ini?",
                            choices = listOf("Lebih dari 10% kalori harian", "Kurang dari 10% kalori harian")
                        ),
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
                    questions = listOf(
                        Question(
                            id = "1",
                            question = "Apakah digoreng?",
                            choices = listOf("Ya", "Tidak")
                        ),
                        Question(
                            id = "2",
                            question = "Berapa kandungan gula dalam makanan ini?",
                            choices = listOf("Lebih dari 10% kalori harian", "Kurang dari 10% kalori harian")
                        ),
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
                    questions = null
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
        feedback = "Terlalu banyak gula",
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
            ),
            Food(
                id = "2",
                name = "Ayam Bakar",
                calorie = 50f,
                protein = 6f,
                fat = 10f,
                carbohydrates = 8.3f,
                sugar = null,
            ),
            Food(
                id = "3",
                name = "Tempe Goreng",
                calorie = 5.8f,
                protein = 9f,
                fat = 1f,
                carbohydrates = 8.3f,
                sugar = 0f,
            ),
            Food(
                id = "4",
                name = "Sayur Kangkung",
                calorie = 5.8f,
                protein = 9f,
                fat = 0.5f,
                carbohydrates = 8.3f,
                sugar = 0f,
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
                label = System.currentTimeMillis().withTimeFormat()
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
                label = "17 Feb"
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
                label = "2020"
            )
        }
    )


    override fun getDiaryFoodsByDays(
        days: Long,
        category: FoodDiaryCategory
    ): Flow<UiState<List<FoodDiary>>> = channelFlow {
        send(UiState.Loading)
        delay(1000L)
        userAccountLocalSource.accessToken.collectLatest { accessToken ->
            accessToken?.let {
                foodDiaryRemoteSource.getFoodDiaries(
                    accessToken = it,
                    getFoodDiaryBodyRequest = GetFoodDiaryBodyRequest(
                        days = days,
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
                                date = convertISOToMillis(foodDiary.addedAt).withFullDateFormat(),
                                time = convertISOToMillis(foodDiary.addedAt).withTimeFormat(),
                                foodPictureUrl = "$DIETARY_API/upload/${foodDiary.filePath}",
                                totalFoodCalories = foodDiary.totalFoodCalories
                            )
                        }))
                    }
                )
            }
        }
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

    override fun addFoodDiary(addFoodDiary: AddFoodDiary): Flow<UiState<FoodDiaryDetail>> =
        channelFlow {
            send(UiState.Loading)
            delay(1000L)
            userAccountLocalSource.userId.combine(userAccountLocalSource.accessToken) { userId, accessToken ->
                Pair(userId, accessToken)
            }.collectLatest {
                if (it.first != null && it.first?.isNotBlank() == true && it.second != null && it.second?.isNotBlank() == true) {
                    val time = getCurrentDateTimeInISOFormat()
                    Log.d("time", time)
                    foodDiaryRemoteSource.addFoodDiary(
                        accessToken = it.second!!,
                        foodDiary = FoodDiaryBodyRequest(
                            title = addFoodDiary.title,
                            category = addFoodDiary.category,
                            foodPicture = addFoodDiary.foodPicture,
                            userAccountId = it.first!!,
                            addedAt = time
                        )
                    ).fold(
                        ifLeft = { exception ->
                            send(UiState.Error(exception))
                        },
                        ifRight = {
                            send(UiState.Success(foodDiaryDetail.value))
                        }
                    )
                }
            }
        }.flowOn(Dispatchers.IO)
            .distinctUntilChanged()

    override fun updateFoodDiaryBaseOnAnsweredQuestion(foodDiaryQuestion: FoodDiaryQuestion): Flow<UiState<FoodDiaryDetail>> =
        flow {
            emit(UiState.Loading)
            delay(1000L)
            emit(UiState.Success(foodDiaryDetail.value.copy(
                status = "Kurang disarankan",
                feedback = "Terlalu banyak gula",
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

    override fun duplicateFoodDiaryById(
        foodDiaryId: String,
        foodDiaryCategory: String
    ): Flow<UiState<Nothing>> = flow {
        emit(UiState.Loading)
        delay(1000L)
        //        emit(UiState.Error(Failure.BadRequestFailure("bad request")))
//        emit(UiState.Error(error = Failure.ConnectionFailure("No internet access")))
        emit(UiState.Success())
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
}