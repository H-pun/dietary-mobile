package dev.cisnux.dietary.data.repositories

import dev.cisnux.dietary.domain.models.FoodDiary
import dev.cisnux.dietary.domain.repositories.FoodRepository
import dev.cisnux.dietary.utils.withDateFormat
import dev.cisnux.dietary.utils.withTimeFormat
import dev.cisnux.dietary.utils.DiaryFoodCategory
import dev.cisnux.dietary.utils.Failure
import dev.cisnux.dietary.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlinx.coroutines.delay

class FoodDiaryRepositoryImpl @Inject constructor() : FoodRepository {
    private val foodsBreakfastDiary = List(10) {
        FoodDiary(
            id = it.toString(),
            foodName = "Mie Ayam",
            date = 1706351552829.withDateFormat(),
            time = 1706351552829.withTimeFormat(),
            foodImageUrl = "https://allofresh.id/blog/wp-content/uploads/2023/08/cara-membuat-mie-ayam-4.jpg",
            calorie = 500f
        )
    }
    private val foodsLunchDiaries = List(10) {
        FoodDiary(
            id = it.toString(),
            foodName = "Nasi Padang",
            date = 1706351552829.withDateFormat(),
            time = 1706351552829.withTimeFormat(),
            foodImageUrl = "https://awsimages.detik.net.id/community/media/visual/2020/07/06/nasi-padang.jpeg?w=600&q=90",
            calorie = 300f
        )
    }
    private val foodsDinnerDiary = List(10) {
        FoodDiary(
            id = it.toString(),
            foodName = "Kwetiau",
            date = 1706351552829.withDateFormat(),
            time = 1706351552829.withTimeFormat(),
            foodImageUrl = "https://img-global.cpcdn.com/recipes/e79696c6cc7f385b/680x482cq70/kwetiau-goreng-simple-dan-tips-merebus-kwetiau-beras-foto-resep-utama.jpg",
            calorie = 200f
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


    override fun getDiaryFoodsByDays(
        days: Long,
        category: DiaryFoodCategory
    ): Flow<UiState<List<FoodDiary>>> = flow {
        emit(UiState.Loading)
        delay(1500L)
//        emit(UiState.Error(Failure.BadRequestFailure("No Internet")))
        emit(
            UiState.Success(
                when (category) {
                    DiaryFoodCategory.BREAKFAST -> foodsBreakfastDiary
                    DiaryFoodCategory.LUNCH -> foodsLunchDiaries
                    DiaryFoodCategory.DINNER -> foodsDinnerDiary
                }
            )
        )
    }.distinctUntilChanged()
        .flowOn(Dispatchers.IO)

    override fun getDiaryFoodsByQuery(query: String): Flow<UiState<List<FoodDiary>>> = flow {
        emit(UiState.Loading)
        delay(1000L)
                emit(UiState.Error(Failure.BadRequestFailure("No Internet")))
//        emit(UiState.Success(listOf()))
//        emit(UiState.Success(diaryFoodsBreakfast))
    }

    override fun getKeywordSuggestionsByQuery(query: String): Flow<UiState<List<String>>> = flow {
        emit(UiState.Loading)
//        emit(UiState.Error(Failure.BadRequestFailure("No Internet")))
        emit(UiState.Success(keywordSuggestions.filter { it.contains(query.lowercase()) }))
    }
}