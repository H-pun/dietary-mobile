package dev.cisnux.dietary.data.repositories

import dev.cisnux.dietary.domain.models.AddFoodDiary
import dev.cisnux.dietary.domain.models.Food
import dev.cisnux.dietary.domain.models.FoodDiary
import dev.cisnux.dietary.domain.models.FoodDiaryQuestion
import dev.cisnux.dietary.domain.models.FoodDiaryDetail
import dev.cisnux.dietary.domain.models.FoodDiaryReport
import dev.cisnux.dietary.domain.models.Question
import dev.cisnux.dietary.domain.models.Report
import dev.cisnux.dietary.domain.repositories.FoodRepository
import dev.cisnux.dietary.utils.withFullDateFormat
import dev.cisnux.dietary.utils.withTimeFormat
import dev.cisnux.dietary.utils.FoodDiaryCategory
import dev.cisnux.dietary.utils.Failure
import dev.cisnux.dietary.utils.QuestionType
import dev.cisnux.dietary.utils.ReportCategory
import dev.cisnux.dietary.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow

class FoodDiaryRepositoryImpl @Inject constructor() : FoodRepository {
    private val foodsBreakfastDiary = List(10) {
        FoodDiary(
            id = it.toString(),
            title = "Mie Ayam",
            date = 1706351552829.withFullDateFormat(),
            time = 1706351552829.withTimeFormat(),
            foodPictureUrl = "https://allofresh.id/blog/wp-content/uploads/2023/08/cara-membuat-mie-ayam-4.jpg",
            totalFoodCalories = 500f
        )
    }
    private val foodsLunchDiaries = List(10) {
        FoodDiary(
            id = it.toString(),
            title = "Nasi Padang",
            date = 1706351552829.withFullDateFormat(),
            time = 1706351552829.withTimeFormat(),
            foodPictureUrl = "https://awsimages.detik.net.id/community/media/visual/2020/07/06/nasi-padang.jpeg?w=600&q=90",
            totalFoodCalories = 300f
        )
    }
    private val foodsDinnerDiary = List(10) {
        FoodDiary(
            id = it.toString(),
            title = "Kwetiau",
            date = 1706351552829.withFullDateFormat(),
            time = 1706351552829.withTimeFormat(),
            foodPictureUrl = "https://img-global.cpcdn.com/recipes/e79696c6cc7f385b/680x482cq70/kwetiau-goreng-simple-dan-tips-merebus-kwetiau-beras-foto-resep-utama.jpg",
            totalFoodCalories = 200f
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
            maxDailyBmiCalorie = 800.6798f,
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
                            label = "Minyak",
                            question = "Apakah makanan digoreng dengan minyak berkali-kali?",
                            type = QuestionType.BOOLEAN,
                            unit = null
                        ),
                        Question(
                            id = "2",
                            label = "Gula",
                            question = "Berapa kandungan gula dalam makanan ini?",
                            type = QuestionType.FLOAT,
                            unit = "g"
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
                            id = "2",
                            label = "Gula",
                            question = "Berapa kandungan gula dalam makanan ini?",
                            type = QuestionType.FLOAT,
                            unit = "g"
                        ),
                    )
                ),
                Food(
                    id = "3",
                    name = "Tempe Goreng",
                    calorie = 5.8f,
                    protein = 9f,
                    fat = 1f,
                    carbohydrates = 8.3f,
                    sugar = 0f,
                    questions = null
                ),
                Food(
                    id = "4",
                    name = "Sayur Kangkung",
                    calorie = 5.8f,
                    protein = 9f,
                    fat = 0.5f,
                    carbohydrates = 8.3f,
                    sugar = 0f,
                    questions = listOf(
                        Question(
                            id = "2",
                            label = "Gula",
                            question = "Berapa kandungan gula dalam makanan ini?",
                            type = QuestionType.FLOAT,
                            unit = "g"
                        ),
                        Question(
                            id = "3",
                            label = "Jumlah",
                            question = "Berapa jumlah makanan ini?",
                            type = QuestionType.INTEGER,
                            unit = null
                        ),
                    )
                ),
            )
        )
    )

    private val anotherFoodDiaryDetail = FoodDiaryDetail(
        foodDiaryId = "1",
        totalFoodCalories = 200.4512f,
        maxDailyBmiCalorie = 800.6798f,
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
        foods = List(5) {
            FoodDiaryReport(
                id = it.toString(),
                title = "Warter $it",
                totalFoodCalories = (20.2144f * it) * 0.2.toFloat(),
                label = System.currentTimeMillis().withTimeFormat()
            )
        }.sortedBy { it.totalFoodCalories }
    )

    private val foodDiaryReportWeek = Report(
        totalUserCaloriesToday = 500.7892f,
        maxDailyBmiCalorie = 800.6798f,
        foods = List(12) {
            FoodDiaryReport(
                id = it.toString(),
                title = "Warter $it",
                totalFoodCalories = (20.2144f * it) * 0.2.toFloat(),
                label = System.currentTimeMillis().withTimeFormat()
            )
        }.sortedBy { it.totalFoodCalories }
    )

    private val foodDiaryReportMonth = Report(
        totalUserCaloriesToday = 500.7892f,
        maxDailyBmiCalorie = 800.6798f,
        foods = List(25) {
            FoodDiaryReport(
                id = it.toString(),
                title = "Warter $it",
                totalFoodCalories = (20.2144f * it) * 0.2.toFloat(),
                label = System.currentTimeMillis().withTimeFormat()
            )
        }.sortedBy { it.totalFoodCalories }
    )


    override fun getDiaryFoodsByDays(
        days: Long,
        category: FoodDiaryCategory
    ): Flow<UiState<List<FoodDiary>>> = flow {
        emit(UiState.Loading)
        delay(1500L)
//        emit(UiState.Error(Failure.BadRequestFailure("bad request")))
        emit(
            UiState.Success(
                when (category) {
                    FoodDiaryCategory.BREAKFAST -> foodsBreakfastDiary
                    FoodDiaryCategory.LUNCH -> foodsLunchDiaries
                    FoodDiaryCategory.DINNER -> foodsDinnerDiary
                }
            )
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

    override fun addFoodDiary(addFoodDiary: AddFoodDiary): Flow<UiState<FoodDiaryDetail>> = flow {
        emit(UiState.Loading)
        delay(1000L)
        emit(UiState.Success(foodDiaryDetail.value))
        //        emit(UiState.Error(Failure.BadRequestFailure("bad request")))
//                            emit(UiState.Error(error = Failure.ConnectionFailure("No internet access")))
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
        emit(UiState.Error(error = Failure.ConnectionFailure("No internet access")))
//        emit(UiState.Success())
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
                        ReportCategory.WEEK -> foodDiaryReportWeek
                        else -> foodDiaryReportMonth
                    }
                )
            )
        }.flowOn(Dispatchers.IO)
            .distinctUntilChanged()
}