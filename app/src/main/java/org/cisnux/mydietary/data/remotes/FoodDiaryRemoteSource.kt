package org.cisnux.mydietary.data.remotes

import arrow.core.Either
import org.cisnux.mydietary.data.remotes.bodyrequests.FoodDiaryBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.GetFoodDiaryParams
import org.cisnux.mydietary.data.remotes.bodyrequests.ReportBodyRequest
import org.cisnux.mydietary.data.remotes.responses.FoodDiaryDetailResponse
import org.cisnux.mydietary.data.remotes.responses.FoodDiaryResponse
import org.cisnux.mydietary.data.remotes.responses.DetectedResponse
import org.cisnux.mydietary.data.remotes.responses.KeywordResponse
import org.cisnux.mydietary.data.remotes.responses.ReportResponse
import java.io.File

interface FoodDiaryRemoteSource {
    suspend fun getFoodDiaries(
        accessToken: String,
        getFoodDiaryParams: GetFoodDiaryParams
    ): Either<Exception, List<FoodDiaryResponse>>

    suspend fun getFoodDiaryById(
        accessToken: String,
        id: String
    ): Either<Exception, FoodDiaryDetailResponse>

    suspend fun addFoodDiary(
        accessToken: String,
        foodDiary: FoodDiaryBodyRequest
    ): Either<Exception, String>

    suspend fun detectFoods(
        accessToken: String,
        foodPicture: File
    ): Either<Exception, DetectedResponse>

    suspend fun getFoodDiaryReports(
        accessToken: String,
        reportBodyRequest: ReportBodyRequest
    ): Either<Exception, List<ReportResponse>>


    suspend fun getSuggestionKeywords(
        accessToken: String,
        getFoodDiaryParams: GetFoodDiaryParams
    ): Either<Exception, List<KeywordResponse>>

    suspend fun deleteFoodDiaryById(accessToken: String, id: String): Either<Exception, Nothing?>
}