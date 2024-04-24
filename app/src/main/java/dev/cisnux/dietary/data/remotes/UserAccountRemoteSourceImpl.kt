package dev.cisnux.dietary.data.remotes

import arrow.core.Either
import dev.cisnux.dietary.data.locals.BaseApiUrlLocalSource
import dev.cisnux.dietary.data.remotes.responses.CommonResponse
import dev.cisnux.dietary.data.remotes.bodyrequests.NewPasswordBodyRequest
import dev.cisnux.dietary.data.remotes.bodyrequests.ResetPasswordBodyRequest
import dev.cisnux.dietary.data.remotes.bodyrequests.UserAccountBodyRequest
import dev.cisnux.dietary.data.remotes.responses.AddedUserAccountResponse
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
import io.ktor.http.isSuccess
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn

class UserAccountRemoteSourceImpl @Inject constructor(
    private val client: HttpClient,
    private val baseApiUrlLocalSource: BaseApiUrlLocalSource
) : UserAccountRemoteSource {
    override suspend fun signIn(userAccount: UserAccountBodyRequest): Either<Exception, AddedUserAccountResponse> =
        withContext(Dispatchers.IO) {
            try {
                val baseUrl = baseApiUrlLocalSource.baseApiUrl.flowOn(Dispatchers.IO).first()
                val response = client.post(
                    urlString = "$baseUrl/user/login"
                ) {
                    contentType(ContentType.Application.Json)
                    setBody(userAccount)
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
                val baseUrl = baseApiUrlLocalSource.baseApiUrl.flowOn(Dispatchers.IO).first()
                val response = client.post(
                    urlString = "$baseUrl/user/register"
                ) {
                    contentType(ContentType.Application.Json)
                    setBody(userAccount)
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
                    Either.Right(null)
                }
            } catch (e: UnresolvedAddressException) {
                Either.Left(Failure.ConnectionFailure())
            }
        }

    override suspend fun resetPassword(resetPassword: ResetPasswordBodyRequest): Either<Exception, String?> =
        withContext(Dispatchers.IO) {
            try {
                val baseUrl = baseApiUrlLocalSource.baseApiUrl.flowOn(Dispatchers.IO).first()
                val response = client.post(
                    urlString = "$baseUrl/password"
                ) {
                    contentType(ContentType.Application.Json)
                    setBody(resetPassword)
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
                val baseUrl = baseApiUrlLocalSource.baseApiUrl.flowOn(Dispatchers.IO).first()
                val response = client.put(
                    urlString = "$baseUrl/password"
                ) {
                    contentType(ContentType.Application.Json)
                    setBody(newPassword)
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
                    val commonResponse: CommonResponse<Nothing> = response.body()
                    Either.Right(commonResponse.message)
                }
            } catch (e: UnresolvedAddressException) {
                Either.Left(Failure.ConnectionFailure())
            }
        }
}