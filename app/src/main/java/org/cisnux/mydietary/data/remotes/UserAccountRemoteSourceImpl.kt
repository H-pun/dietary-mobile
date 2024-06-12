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
import org.cisnux.mydietary.commons.utils.Failure
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import org.cisnux.mydietary.data.remotes.bodyrequests.ChangePasswordBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.GoogleTokenBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.UpdateEmailBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.VerifyEmailBodyRequest

class UserAccountRemoteSourceImpl @Inject constructor(
    private val client: HttpClient,
    private val baseApiUrlLocalSource: BaseApiUrlLocalSource
) : UserAccountRemoteSource {
    override suspend fun signInWithEmailAddressAndPassword(userAccount: UserAccountBodyRequest): Either<Exception, AddedUserAccountResponse> =
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
            } catch (e: Exception) {
                Either.Left(e)
            }
        }

    override suspend fun verifyEmailAddress(
        accessToken: String,
        verifyEmailBodyRequest: VerifyEmailBodyRequest
    ): Either<Exception, String> = withContext(Dispatchers.IO) {
        try {
            val baseUrl = baseApiUrlLocalSource.baseApiUrl.flowOn(Dispatchers.IO).first()
            val response = client.post(
                urlString = "$baseUrl/user/send-email-verification"
            ) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
                contentType(ContentType.Application.Json)
                setBody(verifyEmailBodyRequest)
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
                val commonResponse: CommonResponse<String> = response.body()
                Either.Right(commonResponse.data!!)
            }
        } catch (e: UnresolvedAddressException) {
            Either.Left(Failure.ConnectionFailure())
        } catch (e: Exception) {
            Either.Left(e)
        }
    }

    override suspend fun updateEmail(
        accessToken: String,
        updateEmailBodyRequest: UpdateEmailBodyRequest
    ): Either<Exception, String> = withContext(Dispatchers.IO) {
        try {
            val baseUrl = baseApiUrlLocalSource.baseApiUrl.flowOn(Dispatchers.IO).first()
            val response = client.put(
                urlString = "$baseUrl/user"
            ) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
                contentType(ContentType.Application.Json)
                setBody(updateEmailBodyRequest)
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
                val commonResponse: CommonResponse<String> = response.body()
                Either.Right(commonResponse.data!!)
            }
        } catch (e: UnresolvedAddressException) {
            Either.Left(Failure.ConnectionFailure())
        } catch (e: Exception) {
            Either.Left(e)
        }
    }

    override suspend fun changePassword(
        accessToken: String,
        changePasswordBodyRequest: ChangePasswordBodyRequest
    ): Either<Exception, String> = withContext(Dispatchers.IO) {
        try {
            val baseUrl = baseApiUrlLocalSource.baseApiUrl.flowOn(Dispatchers.IO).first()
            val response = client.post(
                urlString = "$baseUrl/user/change-password"
            ) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
                contentType(ContentType.Application.Json)
                setBody(changePasswordBodyRequest)
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
                val commonResponse: CommonResponse<String> = response.body()
                Either.Right(commonResponse.data!!)
            }
        } catch (e: UnresolvedAddressException) {
            Either.Left(Failure.ConnectionFailure())
        } catch (e: Exception) {
            Either.Left(e)
        }
    }


    override suspend fun signInWithGoogle(googleToken: GoogleTokenBodyRequest): Either<Exception, AddedUserAccountResponse> =
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
            } catch (e: Exception) {
                Either.Left(e)
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
            } catch (e: Exception) {
                Either.Left(e)
            }
        }

    override suspend fun sendResetPassword(resetPassword: ResetPasswordBodyRequest): Either<Exception, String> =
        withContext(Dispatchers.IO) {
            try {
                val baseUrl = baseApiUrlLocalSource.baseApiUrl.flowOn(Dispatchers.IO).first()
                val response = client.post(
                    urlString = "$baseUrl/user/reset-password"
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
                    val commonResponse: CommonResponse<String> = response.body()
                    Either.Right(commonResponse.message)
                }
            } catch (e: UnresolvedAddressException) {
                Either.Left(Failure.ConnectionFailure())
            } catch (e: Exception) {
                Either.Left(e)
            }
        }


    override suspend fun forgotPassword(newPassword: NewPasswordBodyRequest): Either<Exception, String> =
        withContext(Dispatchers.IO) {
            try {
                val baseUrl = baseApiUrlLocalSource.baseApiUrl.flowOn(Dispatchers.IO).first()
                val response = client.post(
                    urlString = "$baseUrl/user/verify-reset-password"
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
                    val commonResponse: CommonResponse<String> = response.body()
                    Either.Right(commonResponse.message)
                }
            } catch (e: UnresolvedAddressException) {
                Either.Left(Failure.ConnectionFailure())
            } catch (e: Exception) {
                Either.Left(e)
            }
        }
}