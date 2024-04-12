package dev.cisnux.dietary.data.remotes

import arrow.core.Either
import dev.cisnux.dietary.data.remotes.responses.AddedFoodDiaryResponse
import dev.cisnux.dietary.data.remotes.bodyrequests.DiaryQuestionBodyRequest
import dev.cisnux.dietary.data.remotes.bodyrequests.FoodDiaryBodyRequest
import dev.cisnux.dietary.data.remotes.bodyrequests.GetFoodDiaryBodyRequest
import dev.cisnux.dietary.data.remotes.responses.FoodDiaryDetailResponse
import dev.cisnux.dietary.data.remotes.responses.FoodDiaryResponse
import dev.cisnux.dietary.data.remotes.responses.PredictedResponse
import dev.cisnux.dietary.data.remotes.responses.ReportResponse
import java.io.File

interface FoodDiaryRemoteSource {
    suspend fun getFoodDiaries(
        accessToken: String,
        getFoodDiaryBodyRequest: GetFoodDiaryBodyRequest
    ): Either<Exception, List<FoodDiaryResponse>>

    suspend fun getFoodDiaryById(
        accessToken: String,
        id: String
    ): Either<Exception, FoodDiaryDetailResponse>

    suspend fun addFoodDiary(
        accessToken: String,
        foodDiary: FoodDiaryBodyRequest
    ): Either<Exception, AddedFoodDiaryResponse>

    suspend fun predictFoods(
        accessToken: String,
        foodPicture: File
    ): Either<Exception, PredictedResponse>

    suspend fun updateFoodDiaryByQuestions(
        accessToken: String,
        foodDiaryQuestion: DiaryQuestionBodyRequest
    ): Either<Exception, AddedFoodDiaryResponse>

    suspend fun getFoodDiaryReports(
        accessToken: String,
        category: Int
    ): Either<Exception, ReportResponse>

    // optional
    suspend fun getKeywordSuggestions(
        accessToken: String,
        query: String
    ): Either<Exception, List<String>>

    suspend fun deleteFoodDiaryById(accessToken: String, id: String): Either<Exception, Nothing?>
}