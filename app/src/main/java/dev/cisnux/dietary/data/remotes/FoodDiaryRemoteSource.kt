package dev.cisnux.dietary.data.remotes

import arrow.core.Either
import dev.cisnux.dietary.data.remotes.responses.AddedFoodDiaryResponse
import dev.cisnux.dietary.data.remotes.responses.DiaryQuestionBodyRequest
import dev.cisnux.dietary.data.remotes.responses.DuplicateFoodDiaryBodyRequest
import dev.cisnux.dietary.data.remotes.responses.FoodDiaryBodyRequest
import dev.cisnux.dietary.data.remotes.responses.FoodDiaryDetailResponse
import dev.cisnux.dietary.data.remotes.responses.FoodDiaryReportResponse
import dev.cisnux.dietary.data.remotes.responses.FoodDiaryResponse
import dev.cisnux.dietary.data.remotes.responses.ReportResponse

interface FoodDiaryRemoteSource {
    suspend fun getFoodDiaries(accessToken: String, days: Int, category: Int, query: String? = null): Either<Exception, List<FoodDiaryResponse>>
    suspend fun getFoodDiaryById(accessToken: String, id: String): Either<Exception, FoodDiaryDetailResponse>
    suspend fun addFoodDiary(accessToken: String, foodDiary: FoodDiaryBodyRequest): Either<Exception, AddedFoodDiaryResponse>
    suspend fun duplicateFoodDiary(accessToken: String, duplicateFoodDiary: DuplicateFoodDiaryBodyRequest): Either<Exception, Nothing?>
    suspend fun updateFoodDiaryByQuestions(accessToken: String, foodDiaryQuestion: DiaryQuestionBodyRequest): Either<Exception, AddedFoodDiaryResponse>
    suspend fun getFoodDiaryReports(accessToken: String, category: Int): Either<Exception, ReportResponse>
    // optional
    suspend fun getKeywordSuggestions(accessToken: String, query: String): Either<Exception, List<String>>
}