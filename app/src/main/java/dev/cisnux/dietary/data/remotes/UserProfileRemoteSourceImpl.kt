package dev.cisnux.dietary.data.remotes

import arrow.core.Either
import dev.cisnux.dietary.data.remotes.responses.CommonResponse
import dev.cisnux.dietary.data.remotes.bodyrequests.NewUserProfileBodyRequest
import dev.cisnux.dietary.data.remotes.bodyrequests.UpdateUserProfileBodyRequest
import dev.cisnux.dietary.data.remotes.responses.UserProfileDetailResponse
import dev.cisnux.dietary.utils.DIETARY_API
import dev.cisnux.dietary.utils.Failure
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserProfileRemoteSourceImpl @Inject constructor(
    private val client: HttpClient
) : UserProfileRemoteSource {
    override suspend fun addUserProfile(
        accessToken: String,
        userProfile: NewUserProfileBodyRequest
    ): Either<Exception, Nothing?> =
        withContext(Dispatchers.IO) {
            try {
                val response = client.post(
                    urlString = "$DIETARY_API/user-data"
                ) {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $accessToken")
                    }
                    contentType(ContentType.Application.Json)
                    setBody(userProfile)
                }
                val failure = Failure.HTTP_FAILURES[response.status]
                return@withContext if (failure != null) {
                    val commonResponse: CommonResponse<Nothing> = response.body()
                    Either.Left(failure.apply {
                        message = commonResponse.message
                    })
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
                val response = client.put(
                    urlString = "$DIETARY_API/user-data"
                ) {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $accessToken")
                    }
                    contentType(ContentType.Application.Json)
                    setBody(userProfile)
                }
                val failure = Failure.HTTP_FAILURES[response.status]
                return@withContext if (failure != null) {
                    val commonResponse: CommonResponse<Nothing> = response.body()
                    Either.Left(failure.apply {
                        message = commonResponse.message
                    })
                } else Either.Right(null)
            } catch (e: UnresolvedAddressException) {
                Either.Left(Failure.ConnectionFailure())
            }
        }


    override suspend fun getUserProfile(accessToken: String, userAccountId: String): Either<Exception, UserProfileDetailResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = client.get(
                    urlString = "$DIETARY_API/user-data/$userAccountId"
                ) {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $accessToken")
                    }
                }
                val failure = Failure.HTTP_FAILURES[response.status]
                return@withContext if (failure != null) {
                    val commonResponse: CommonResponse<Nothing> = response.body()
                    Either.Left(failure.apply {
                        message = commonResponse.message
                    })
                } else {
                    val commonResponse: CommonResponse<UserProfileDetailResponse> = response.body()
                    Either.Right(commonResponse.data!!)
                }
            } catch (e: UnresolvedAddressException) {
                Either.Left(Failure.ConnectionFailure())
            }
        }
}