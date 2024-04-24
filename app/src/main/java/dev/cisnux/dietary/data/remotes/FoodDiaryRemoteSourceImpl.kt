package dev.cisnux.dietary.data.remotes

import arrow.core.Either
import dev.cisnux.dietary.data.locals.BaseApiUrlLocalSource
import dev.cisnux.dietary.data.remotes.responses.AddedFoodDiaryResponse
import dev.cisnux.dietary.data.remotes.responses.CommonResponse
import dev.cisnux.dietary.data.remotes.bodyrequests.DiaryQuestionBodyRequest
import dev.cisnux.dietary.data.remotes.bodyrequests.FoodDiaryBodyRequest
import dev.cisnux.dietary.data.remotes.bodyrequests.GetFoodDiaryBodyRequest
import dev.cisnux.dietary.data.remotes.responses.FoodDiaryDetailResponse
import dev.cisnux.dietary.data.remotes.responses.FoodDiaryResponse
import dev.cisnux.dietary.data.remotes.responses.PredictedResponse
import dev.cisnux.dietary.data.remotes.responses.ReportResponse
import dev.cisnux.dietary.utils.Failure
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
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
import io.ktor.http.isSuccess
import io.ktor.util.network.UnresolvedAddressException
import io.ktor.utils.io.streams.asInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class FoodDiaryRemoteSourceImpl @Inject constructor(
    private val client: HttpClient,
    private val baseApiUrlLocalSource: BaseApiUrlLocalSource
) : FoodDiaryRemoteSource {
    override suspend fun getFoodDiaries(
        accessToken: String, getFoodDiaryBodyRequest: GetFoodDiaryBodyRequest
    ): Either<Exception, List<FoodDiaryResponse>> = withContext(Dispatchers.IO) {
        try {
            val baseUrl = baseApiUrlLocalSource.baseApiUrl.flowOn(Dispatchers.IO).first()
            val response =
                client.get(urlString = "$baseUrl/food-diary/user?idUser=${getFoodDiaryBodyRequest.userId}&date=${getFoodDiaryBodyRequest.date}") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $accessToken")
                    }
                    contentType(ContentType.Application.Json)
                }
            val isSuccess = response.status.isSuccess()
            return@withContext if (!isSuccess) {
                val commonResponse: CommonResponse<Nothing> = response.body()
                val failure = Failure.HTTP_FAILURES[response.status]?.let {
                    Either.Left(it.apply {
                        message = commonResponse.message
                    })
                } ?: Either.Left(Exception(commonResponse.message))
                failure
            } else {
                val commonResponse: CommonResponse<List<FoodDiaryResponse>> = response.body()
                Either.Right(commonResponse.data!!)
            }
        } catch (e: UnresolvedAddressException) {
            Either.Left(Failure.ConnectionFailure())
        }
    }

    override suspend fun getFoodDiaryById(
        accessToken: String, id: String
    ): Either<Exception, FoodDiaryDetailResponse> = withContext(Dispatchers.IO) {
        try {
            val baseUrl = baseApiUrlLocalSource.baseApiUrl.flowOn(Dispatchers.IO).first()
            val response = client.get(
                urlString = "$baseUrl/food-diary/$id"
            ) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
            }
            val isSuccess = response.status.isSuccess()
            return@withContext if (!isSuccess) {
                val commonResponse: CommonResponse<Nothing> = response.body()
                val failure = Failure.HTTP_FAILURES[response.status]?.let {
                    Either.Left(it.apply {
                        message = commonResponse.message
                    })
                } ?: Either.Left(Exception(commonResponse.message))
                failure
            } else {
                val commonResponse: CommonResponse<FoodDiaryDetailResponse> = response.body()
                Either.Right(commonResponse.data!!)
            }
        } catch (e: UnresolvedAddressException) {
            Either.Left(Failure.ConnectionFailure())
        }
    }

    override suspend fun addFoodDiary(
        accessToken: String, foodDiary: FoodDiaryBodyRequest
    ): Either<Exception, AddedFoodDiaryResponse> = withContext(Dispatchers.IO) {
        try {
            val baseUrl = baseApiUrlLocalSource.baseApiUrl.flowOn(Dispatchers.IO).first()
            val response = client.post(
                urlString = "$baseUrl/food-diary"
            ) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
                setBody(
                    MultiPartFormDataContent(
                        parts = formData {
                            append(key = "IdUser", foodDiary.userAccountId, Headers.build {
                                append(HttpHeaders.ContentType, "text/plain")
                            })
                            append(key = "Title", foodDiary.title, Headers.build {
                                append(HttpHeaders.ContentType, "text/plain")
                            })
                            append(key = "Category", foodDiary.category, Headers.build {
                                append(HttpHeaders.ContentType, "text/plain")
                            })
                            append(
                                key = "AddedAt",
                                foodDiary.addedAt,
                                Headers.build {
                                    append(HttpHeaders.ContentType, "text/plain")
                                })
                            append(
                                key = "File",
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

            val isSuccess = response.status.isSuccess()
            return@withContext if (!isSuccess) {
                val commonResponse: CommonResponse<Nothing> = response.body()
                val failure = Failure.HTTP_FAILURES[response.status]?.let {
                    Either.Left(it.apply {
                        message = commonResponse.message
                    })
                } ?: Either.Left(Exception(commonResponse.message))
                failure
            } else {
                val commonResponse: CommonResponse<AddedFoodDiaryResponse> = response.body()
                Either.Right(commonResponse.data!!)
            }
        } catch (e: UnresolvedAddressException) {
            Either.Left(Failure.ConnectionFailure())
        }
    }

    override suspend fun predictFoods(
        accessToken: String,
        foodPicture: File
    ): Either<Exception, PredictedResponse> =
        withContext(Dispatchers.IO) {
            try {
                val baseUrl = baseApiUrlLocalSource.baseApiUrl.flowOn(Dispatchers.IO).first()
                val response = client.post(
                    urlString = "$baseUrl/food/predict"
                ) {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $accessToken")
                    }
                    setBody(
                        MultiPartFormDataContent(
                            parts = formData {
                                append(
                                    key = "imgFile",
                                    InputProvider(foodPicture.length()) {
                                        foodPicture.inputStream().asInput()
                                    },
                                    Headers.build {
                                        append(HttpHeaders.ContentType, "image/jpg")
                                        append(
                                            HttpHeaders.ContentDisposition,
                                            "filename=${foodPicture.name}"
                                        )
                                    }
                                )
                            },
                            boundary = "WebAppBoundary"
                        )
                    )
                }

                val isSuccess = response.status.isSuccess()
                return@withContext if (!isSuccess) {
                    val commonResponse: CommonResponse<Nothing> = response.body()
                    val failure = Failure.HTTP_FAILURES[response.status]?.let {
                        Either.Left(it.apply {
                            message = commonResponse.message
                        })
                    } ?: Either.Left(Exception(commonResponse.message))
                    failure
                } else {
                    val commonResponse: CommonResponse<PredictedResponse> = response.body()
                    Either.Right(commonResponse.data!!)
                }
            } catch (e: UnresolvedAddressException) {
                Either.Left(Failure.ConnectionFailure())
            }
        }


    override suspend fun updateFoodDiaryByQuestions(
        accessToken: String, foodDiaryQuestion: DiaryQuestionBodyRequest
    ): Either<Exception, AddedFoodDiaryResponse> = withContext(Dispatchers.IO) {
        try {
            val baseUrl = baseApiUrlLocalSource.baseApiUrl.flowOn(Dispatchers.IO).first()
            val response = client.put(
                urlString = "$baseUrl/food_diary"
            ) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
                contentType(ContentType.Application.Json)
                setBody(foodDiaryQuestion)
            }
            val isSuccess = response.status.isSuccess()
            return@withContext if (!isSuccess) {
                val commonResponse: CommonResponse<Nothing> = response.body()
                val failure = Failure.HTTP_FAILURES[response.status]?.let {
                    Either.Left(it.apply {
                        message = commonResponse.message
                    })
                } ?: Either.Left(Exception(commonResponse.message))
                failure
            } else {
                val commonResponse: CommonResponse<AddedFoodDiaryResponse> = response.body()
                Either.Right(commonResponse.data!!)
            }
        } catch (e: UnresolvedAddressException) {
            Either.Left(Failure.ConnectionFailure())
        }
    }

    override suspend fun getFoodDiaryReports(
        accessToken: String, category: Int
    ): Either<Exception, ReportResponse> = withContext(Dispatchers.IO) {
        try {
            val baseUrl = baseApiUrlLocalSource.baseApiUrl.flowOn(Dispatchers.IO).first()
            val response = client.get(
                urlString = "$baseUrl/report?category=$category"
            ) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
            }
            val isSuccess = response.status.isSuccess()
            return@withContext if (!isSuccess) {
                val commonResponse: CommonResponse<Nothing> = response.body()
                val failure = Failure.HTTP_FAILURES[response.status]?.let {
                    Either.Left(it.apply {
                        message = commonResponse.message
                    })
                } ?: Either.Left(Exception(commonResponse.message))
                failure
            } else {
                val commonResponse: CommonResponse<ReportResponse> = response.body()
                Either.Right(commonResponse.data!!)
            }
        } catch (e: UnresolvedAddressException) {
            Either.Left(Failure.ConnectionFailure())
        }
    }

    override suspend fun getKeywordSuggestions(
        accessToken: String, query: String
    ): Either<Exception, List<String>> = withContext(Dispatchers.IO) {
        try {
            val baseUrl = baseApiUrlLocalSource.baseApiUrl.flowOn(Dispatchers.IO).first()
            val response = client.get(
                urlString = "$baseUrl/keyword?query=$query"
            ) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
            }
            val isSuccess = response.status.isSuccess()
            return@withContext if (!isSuccess) {
                val commonResponse: CommonResponse<Nothing> = response.body()
                val failure = Failure.HTTP_FAILURES[response.status]?.let {
                    Either.Left(it.apply {
                        message = commonResponse.message
                    })
                } ?: Either.Left(Exception(commonResponse.message))
                failure
            } else {
                val commonResponse: CommonResponse<List<String>> = response.body()
                Either.Right(commonResponse.data!!)
            }
        } catch (e: UnresolvedAddressException) {
            Either.Left(Failure.ConnectionFailure())
        }
    }

    override suspend fun deleteFoodDiaryById(
        accessToken: String,
        id: String
    ): Either<Exception, Nothing?> = withContext(Dispatchers.IO) {
        try {
            val baseUrl = baseApiUrlLocalSource.baseApiUrl.flowOn(Dispatchers.IO).first()
            val response = client.delete(
                urlString = "$baseUrl/food_diary/$id"
            ) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
            }
            val isSuccess = response.status.isSuccess()
            return@withContext if (!isSuccess) {
                val commonResponse: CommonResponse<Nothing> = response.body()
                val failure = Failure.HTTP_FAILURES[response.status]?.let {
                    Either.Left(it.apply {
                        message = commonResponse.message
                    })
                } ?: Either.Left(Exception(commonResponse.message))
                failure
            } else
                Either.Right(null)
        } catch (e: UnresolvedAddressException) {
            Either.Left(Failure.ConnectionFailure())
        }
    }
}