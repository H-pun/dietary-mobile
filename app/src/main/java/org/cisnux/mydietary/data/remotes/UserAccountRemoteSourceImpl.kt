package org.cisnux.mydietary.data.remotes

import arrow.core.Either
import org.cisnux.mydietary.data.locals.BaseApiUrlLocalSource
import org.cisnux.mydietary.data.remotes.responses.CommonResponse
import org.cisnux.mydietary.data.remotes.bodyrequests.NewPasswordBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.ResetPasswordBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.UserAccountBodyRequest
import org.cisnux.mydietary.data.remotes.responses.AddedUserAccountResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import org.cisnux.mydietary.utils.Failure
import io.ktor.client.call.body
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import org.cisnux.mydietary.data.remotes.bodyrequests.GoogleTokenRequest

class UserAccountRemoteSourceImpl @Inject constructor(
    private val client: HttpClient,
    private val baseApiUrlLocalSource: BaseApiUrlLocalSource
) : UserAccountRemoteSource {
    override suspend fun verifyUserAccount(userAccount: UserAccountBodyRequest): Either<Exception, AddedUserAccountResponse> =
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
            } catch (e: Exception){
                Either.Left(e)
            }
        }

    override suspend fun verifyGoogleAccount(googleToken: GoogleTokenRequest): Either<Exception, AddedUserAccountResponse> =
        withContext(Dispatchers.IO) {
            try {
                val baseUrl = baseApiUrlLocalSource.baseApiUrl.flowOn(Dispatchers.IO).first()
                val response = client.post(
                    urlString = "$baseUrl/user/firebase-auth"
                ) {
                    contentType(ContentType.Application.Json)
                    setBody(googleToken)
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
            } catch (e: Exception){
                Either.Left(e)
            }
        }

    override suspend fun addUserAccount(userAccount: UserAccountBodyRequest): Either<Exception, Nothing?> =
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
            } catch (e: Exception){
                Either.Left(e)
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
            } catch (e: Exception){
                Either.Left(e)
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
            } catch (e: Exception){
                Either.Left(e)
            }
        }
}