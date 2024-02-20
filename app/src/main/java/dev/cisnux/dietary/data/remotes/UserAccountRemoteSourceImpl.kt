package dev.cisnux.dietary.data.remotes

import arrow.core.Either
import dev.cisnux.dietary.data.remotes.responses.CommonResponse
import dev.cisnux.dietary.data.remotes.bodyrequests.NewPasswordBodyRequest
import dev.cisnux.dietary.data.remotes.bodyrequests.ResetPasswordBodyRequest
import dev.cisnux.dietary.data.remotes.bodyrequests.UserAccountBodyRequest
import dev.cisnux.dietary.data.remotes.responses.AddedUserAccountResponse
import dev.cisnux.dietary.utils.DIETARY_API
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import dev.cisnux.dietary.utils.Failure
import io.ktor.client.call.body
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException

class UserAccountRemoteSourceImpl @Inject constructor(
    private val client: HttpClient
) : UserAccountRemoteSource {
    override suspend fun signIn(userAccount: UserAccountBodyRequest): Either<Exception, AddedUserAccountResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = client.post(
                    urlString = "$DIETARY_API/user/login"
                ) {
                    contentType(ContentType.Application.Json)
                    setBody(userAccount)
                }
                val failure = Failure.HTTP_FAILURES[response.status]
                return@withContext if (failure != null) {
                    val commonResponse: CommonResponse<Nothing> = response.body()
                    Either.Left(failure.apply {
                        message = commonResponse.message
                    })
                } else {
                    val commonResponse: CommonResponse<AddedUserAccountResponse> = response.body()
                    Either.Right(commonResponse.data!!)
                }
            } catch (e: UnresolvedAddressException) {
                Either.Left(Failure.ConnectionFailure())
            }
        }

    override suspend fun signUp(userAccount: UserAccountBodyRequest): Either<Exception, Nothing?> =
        withContext(Dispatchers.IO) {
            try {
                val response = client.post(
                    urlString = "$DIETARY_API/user/register"
                ) {
                    contentType(ContentType.Application.Json)
                    setBody(userAccount)
                }
                val failure = Failure.HTTP_FAILURES[response.status]
                return@withContext if (failure != null) {
                    val commonResponse: CommonResponse<Nothing> = response.body()
                    Either.Left(failure.apply {
                        message = commonResponse.message
                    })
                } else {
                    Either.Right(null)
                }
            } catch (e: UnresolvedAddressException) {
                Either.Left(Failure.ConnectionFailure())
            }
        }

    override suspend fun resetPassword(resetPassword: ResetPasswordBodyRequest): Either<Exception, String?> =
        withContext(Dispatchers.IO) {
            try {
                val response = client.post(
                    urlString = "$DIETARY_API/password"
                ){
                    contentType(ContentType.Application.Json)
                    setBody(resetPassword)
                }
                val failure = Failure.HTTP_FAILURES[response.status]
                return@withContext if (failure != null) {
                    val commonResponse: CommonResponse<Nothing> = response.body()
                    Either.Left(failure.apply {
                        message = commonResponse.message
                    })
                } else {
                    val commonResponse: CommonResponse<Nothing> = response.body()
                    Either.Right(commonResponse.message)
                }
            } catch (e: UnresolvedAddressException) {
                Either.Left(Failure.ConnectionFailure())
            }
        }


    override suspend fun newPassword(newPassword: NewPasswordBodyRequest): Either<Exception, String?> =
        withContext(Dispatchers.IO) {
            try {
                val response = client.put(
                    urlString = "$DIETARY_API/password"
                ){
                    contentType(ContentType.Application.Json)
                    setBody(newPassword)
                }

                val failure = Failure.HTTP_FAILURES[response.status]
                return@withContext if (failure != null) {
                    val commonResponse: CommonResponse<Nothing> = response.body()
                    Either.Left(failure.apply {
                        message = commonResponse.message
                    })
                } else {
                    val commonResponse: CommonResponse<Nothing> = response.body()
                    Either.Right(commonResponse.message)
                }
            } catch (e: UnresolvedAddressException) {
                Either.Left(Failure.ConnectionFailure())
            }
        }
}