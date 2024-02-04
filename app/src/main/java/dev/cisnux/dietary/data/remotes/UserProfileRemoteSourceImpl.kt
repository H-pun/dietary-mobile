package dev.cisnux.dietary.data.remotes

import arrow.core.Either
import dev.cisnux.dietary.data.remotes.responses.CommonResponse
import dev.cisnux.dietary.data.remotes.responses.UserProfileBodyRequest
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
        userProfile: UserProfileBodyRequest
    ): Either<Exception, Nothing?> =
        withContext(Dispatchers.IO) {
            try {
                val response = client.post(
                    urlString = "$DIETARY_API/user_profile"
                ) {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $accessToken")
                    }
                    contentType(ContentType.Application.Json)
                    setBody(userProfile)
                }
                val failure = Failure.HTTP_FAILURES[response.status]
                return@withContext if (failure != null) {
                    Either.Left(failure)
                } else Either.Right(null)
            } catch (e: UnresolvedAddressException) {
                Either.Left(Failure.ConnectionFailure())
            }
        }

    override suspend fun updateUserProfile(
        accessToken: String,
        userProfile: UserProfileBodyRequest
    ): Either<Exception, Nothing?> =
        withContext(Dispatchers.IO) {
            try {
                val response = client.put(
                    urlString = "$DIETARY_API/user_profile"
                ) {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $accessToken")
                    }
                    contentType(ContentType.Application.Json)
                    setBody(userProfile)
                }
                val failure = Failure.HTTP_FAILURES[response.status]
                return@withContext if (failure != null) {
                    Either.Left(failure)
                } else Either.Right(null)
            } catch (e: UnresolvedAddressException) {
                Either.Left(Failure.ConnectionFailure())
            }
        }


    override suspend fun getUserProfile(accessToken: String): Either<Exception, UserProfileDetailResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = client.get(
                    urlString = "$DIETARY_API/user_profile"
                ) {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $accessToken")
                    }
                }
                val failure = Failure.HTTP_FAILURES[response.status]
                return@withContext if (failure != null) {
                    Either.Left(failure)
                } else {
                    val commonResponse: CommonResponse<UserProfileDetailResponse> = response.body()
                    Either.Right(commonResponse.data)
                }
            } catch (e: UnresolvedAddressException) {
                Either.Left(Failure.ConnectionFailure())
            }
        }
}