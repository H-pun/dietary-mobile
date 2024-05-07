package org.cisnux.mydietary.data.remotes

import android.util.Log
import arrow.core.Either
import org.cisnux.mydietary.data.locals.BaseApiUrlLocalSource
import org.cisnux.mydietary.data.remotes.responses.CommonResponse
import org.cisnux.mydietary.data.remotes.bodyrequests.NewUserProfileBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.UpdateUserProfileBodyRequest
import org.cisnux.mydietary.data.remotes.responses.NutrientResponse
import org.cisnux.mydietary.data.remotes.responses.UserProfileDetailResponse
import org.cisnux.mydietary.utils.Failure
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserProfileRemoteSourceImpl @Inject constructor(
    private val client: HttpClient,
    private val baseApiUrlLocalSource: BaseApiUrlLocalSource
) : UserProfileRemoteSource {
    override suspend fun addUserProfile(
        accessToken: String,
        userProfile: NewUserProfileBodyRequest
    ): Either<Exception, Nothing?> =
        withContext(Dispatchers.IO) {
            try {
                val baseUrl = baseApiUrlLocalSource.baseApiUrl.flowOn(Dispatchers.IO).first()
                val response = client.post(
                    urlString = "$baseUrl/user-data"
                ) {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $accessToken")
                    }
                    contentType(ContentType.Application.Json)
                    setBody(userProfile)
                }
                val isSuccess = response.status.isSuccess()
                return@withContext if (!isSuccess) {
                    val commonResponse: CommonResponse<Nothing>? =
                        if (response.status != HttpStatusCode.Unauthorized) response.body()
                        else null
                    val failure = Failure.HTTP_FAILURES[response.status]?.let {
                        Either.Left(it.apply {
                            message = commonResponse?.message
                        })
                    } ?: Either.Left(Exception(commonResponse?.message))
                    failure
                } else Either.Right(null)
            } catch (e: UnresolvedAddressException) {
                Either.Left(Failure.ConnectionFailure())
            }
        }

    override suspend fun updateUserProfile(
        accessToken: String,
        userProfile: UpdateUserProfileBodyRequest
    ): Either<Exception, Nothing?> =
        withContext(Dispatchers.IO) {
            try {
                val baseUrl = baseApiUrlLocalSource.baseApiUrl.flowOn(Dispatchers.IO).first()
                val response = client.put(
                    urlString = "$baseUrl/user-data"
                ) {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $accessToken")
                    }
                    contentType(ContentType.Application.Json)
                    Log.d(UserProfileRemoteSource::class.simpleName, userProfile.toString())
                    setBody(userProfile)
                }
                val isSuccess = response.status.isSuccess()
                return@withContext if (!isSuccess) {
                    val commonResponse: CommonResponse<Nothing>? =
                        if (response.status != HttpStatusCode.Unauthorized) response.body()
                        else null
                    val failure = Failure.HTTP_FAILURES[response.status]?.let {
                        Either.Left(it.apply {
                            message = commonResponse?.message
                        })
                    } ?: Either.Left(Exception(commonResponse?.message))
                    failure
                } else Either.Right(null)
            } catch (e: UnresolvedAddressException) {
                Either.Left(Failure.ConnectionFailure())
            }
        }


    override suspend fun getUserProfile(
        accessToken: String,
        userAccountId: String
    ): Either<Exception, UserProfileDetailResponse> =
        withContext(Dispatchers.IO) {
            try {
                val baseUrl = baseApiUrlLocalSource.baseApiUrl.flowOn(Dispatchers.IO).first()
                val response = client.get(
                    urlString = "$baseUrl/user-data/$userAccountId"
                ) {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $accessToken")
                    }
                }
                val isSuccess = response.status.isSuccess()
                return@withContext if (!isSuccess) {
                    val commonResponse: CommonResponse<Nothing>? =
                        if (response.status != HttpStatusCode.Unauthorized) response.body()
                        else null
                    val failure = Failure.HTTP_FAILURES[response.status]?.let {
                        Either.Left(it.apply {
                            message = commonResponse?.message
                        })
                    } ?: Either.Left(Exception(commonResponse?.message))
                    failure
                } else {
                    val commonResponse: CommonResponse<UserProfileDetailResponse> = response.body()
                    Either.Right(commonResponse.data!!)
                }
            } catch (e: UnresolvedAddressException) {
                Either.Left(Failure.ConnectionFailure())
            }
        }

    override suspend fun getDailyNutrients(
        accessToken: String,
        userId: String,
        date: String
    ): Either<Exception, NutrientResponse> = withContext(Dispatchers.IO) {
        try {
            val baseUrl = baseApiUrlLocalSource.baseApiUrl.flowOn(Dispatchers.IO).first()
            val response =
                client.get(urlString = "$baseUrl/food-diary/daily-nutrients?idUser=$userId&date=$date") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $accessToken")
                    }
                    contentType(ContentType.Application.Json)
                }
            val isSuccess = response.status.isSuccess()
            return@withContext if (!isSuccess) {
                val commonResponse: CommonResponse<Nothing>? =
                    if (response.status != HttpStatusCode.Unauthorized) response.body()
                    else null
                val failure = Failure.HTTP_FAILURES[response.status]?.let {
                    Either.Left(it.apply {
                        message = commonResponse?.message
                    })
                } ?: Either.Left(Exception(commonResponse?.message))
                failure
            } else {
                val commonResponse: CommonResponse<NutrientResponse> = response.body()
                Either.Right(
                    commonResponse.data ?: NutrientResponse(
                        calories = 0f,
                        protein = 0f,
                        fat = 0f,
                        carbohydrate = 0f
                    )
                )
            }
        } catch (e: UnresolvedAddressException) {
            Either.Left(Failure.ConnectionFailure())
        }
    }
}