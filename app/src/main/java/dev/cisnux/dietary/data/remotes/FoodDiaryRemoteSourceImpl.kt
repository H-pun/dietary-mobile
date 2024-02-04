package dev.cisnux.dietary.data.remotes

import arrow.core.Either
import dev.cisnux.dietary.data.remotes.responses.AddedFoodDiaryResponse
import dev.cisnux.dietary.data.remotes.responses.CommonResponse
import dev.cisnux.dietary.data.remotes.responses.DiaryQuestionBodyRequest
import dev.cisnux.dietary.data.remotes.responses.DuplicateFoodDiaryBodyRequest
import dev.cisnux.dietary.data.remotes.responses.FoodDiaryBodyRequest
import dev.cisnux.dietary.data.remotes.responses.FoodDiaryDetailResponse
import dev.cisnux.dietary.data.remotes.responses.FoodDiaryResponse
import dev.cisnux.dietary.data.remotes.responses.ReportResponse
import dev.cisnux.dietary.utils.DIETARY_API
import dev.cisnux.dietary.utils.Failure
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.InputProvider
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException
import io.ktor.utils.io.streams.asInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FoodDiaryRemoteSourceImpl @Inject constructor(
    private val client: HttpClient
) : FoodDiaryRemoteSource {
    override suspend fun getFoodDiaries(
        accessToken: String, days: Int, category: Int, query: String?
    ): Either<Exception, List<FoodDiaryResponse>> = withContext(Dispatchers.IO) {
        try {
            val response =
                client.get(urlString = query?.let { "$DIETARY_API/food_diary?days=$days&category=$category&query=$query" }
                    ?: "$DIETARY_API/food_diary?days=$days&category=$category") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $accessToken")
                    }
                }
            val failure = Failure.HTTP_FAILURES[response.status]
            return@withContext if (failure != null) {
                Either.Left(failure)
            } else {
                val commonResponse: CommonResponse<List<FoodDiaryResponse>> = response.body()
                Either.Right(commonResponse.data)
            }
        } catch (e: UnresolvedAddressException) {
            Either.Left(Failure.ConnectionFailure())
        }
    }

    override suspend fun getFoodDiaryById(
        accessToken: String, id: String
    ): Either<Exception, FoodDiaryDetailResponse> = withContext(Dispatchers.IO) {
        try {
            val response = client.get(
                urlString = "$DIETARY_API/food_diary/$id"
            ) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
            }
            val failure = Failure.HTTP_FAILURES[response.status]
            return@withContext if (failure != null) {
                Either.Left(failure)
            } else {
                val commonResponse: CommonResponse<FoodDiaryDetailResponse> = response.body()
                Either.Right(commonResponse.data)
            }
        } catch (e: UnresolvedAddressException) {
            Either.Left(Failure.ConnectionFailure())
        }
    }

    override suspend fun addFoodDiary(
        accessToken: String, foodDiary: FoodDiaryBodyRequest
    ): Either<Exception, AddedFoodDiaryResponse> = withContext(Dispatchers.IO) {
        try {
            val response = client.post(
                urlString = "$DIETARY_API/food_diary"
            ) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
                setBody(
                    MultiPartFormDataContent(
                        parts = formData {
                            append(key = "title", foodDiary.title, Headers.build {
                                append(HttpHeaders.ContentType, "text/plain")
                            })
                            append(key = "category", foodDiary.category, Headers.build {
                                append(HttpHeaders.ContentType, "text/plain")
                            })
                            append(key = "addded_at", foodDiary.addedAt.toString(), Headers.build {
                                append(HttpHeaders.ContentType, "text/plain")
                            })
                            append(
                                key = "food_picture",
                                InputProvider(foodDiary.foodPicture.length()) {
                                    foodDiary.foodPicture.inputStream().asInput()
                                },
                                Headers.build {
                                    append(HttpHeaders.ContentType, "image/jpg")
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=${foodDiary.foodPicture.name}"
                                    )
                                }
                            )
                        },
                        boundary = "WebAppBoundary"
                    )
                )
            }
            val failure = Failure.HTTP_FAILURES[response.status]
            return@withContext if (failure != null) {
                Either.Left(failure)
            } else {
                val commonResponse: CommonResponse<AddedFoodDiaryResponse> = response.body()
                Either.Right(commonResponse.data)
            }
        } catch (e: UnresolvedAddressException) {
            Either.Left(Failure.ConnectionFailure())
        }
    }

    override suspend fun duplicateFoodDiary(
        accessToken: String, duplicateFoodDiary: DuplicateFoodDiaryBodyRequest
    ): Either<Exception, Nothing?> = withContext(Dispatchers.IO) {
        try {
            val response = client.post(
                urlString = "$DIETARY_API/food_diary?is_duplicate=true"
            ) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
                contentType(ContentType.Application.Json)
                setBody(duplicateFoodDiary)
            }
            val failure = Failure.HTTP_FAILURES[response.status]
            return@withContext if (failure != null) {
                Either.Left(failure)
            } else Either.Right(null)
        } catch (e: UnresolvedAddressException) {
            Either.Left(Failure.ConnectionFailure())
        }
    }

    override suspend fun updateFoodDiaryByQuestions(
        accessToken: String, foodDiaryQuestion: DiaryQuestionBodyRequest
    ): Either<Exception, AddedFoodDiaryResponse> = withContext(Dispatchers.IO) {
        try {
            val response = client.put(
                urlString = "$DIETARY_API/food_diary"
            ) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
                contentType(ContentType.Application.Json)
                setBody(foodDiaryQuestion)
            }
            val failure = Failure.HTTP_FAILURES[response.status]
            return@withContext if (failure != null) {
                Either.Left(failure)
            } else {
                val commonResponse: CommonResponse<AddedFoodDiaryResponse> = response.body()
                Either.Right(commonResponse.data)
            }
        } catch (e: UnresolvedAddressException) {
            Either.Left(Failure.ConnectionFailure())
        }
    }

    override suspend fun getFoodDiaryReports(
        accessToken: String, category: Int
    ): Either<Exception, ReportResponse> = withContext(Dispatchers.IO) {
        try {
            val response = client.get(
                urlString = "$DIETARY_API/report?category=$category"
            ) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
            }
            val failure = Failure.HTTP_FAILURES[response.status]
            return@withContext if (failure != null) {
                Either.Left(failure)
            } else {
                val commonResponse: CommonResponse<ReportResponse> = response.body()
                Either.Right(commonResponse.data)
            }
        } catch (e: UnresolvedAddressException) {
            Either.Left(Failure.ConnectionFailure())
        }
    }

    override suspend fun getKeywordSuggestions(
        accessToken: String, query: String
    ): Either<Exception, List<String>> = withContext(Dispatchers.IO) {
        try {
            val response = client.get(
                urlString = "$DIETARY_API/keyword?query=$query"
            ) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
            }
            val failure = Failure.HTTP_FAILURES[response.status]
            return@withContext if (failure != null) {
                Either.Left(failure)
            } else {
                val commonResponse: CommonResponse<List<String>> = response.body()
                Either.Right(commonResponse.data)
            }
        } catch (e: UnresolvedAddressException) {
            Either.Left(Failure.ConnectionFailure())
        }
    }
}