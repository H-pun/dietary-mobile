package dev.cisnux.dietary.data.remotes

import arrow.core.Either
import dev.cisnux.dietary.data.remotes.responses.CommonResponse
import dev.cisnux.dietary.data.remotes.responses.NewPasswordBodyRequest
import dev.cisnux.dietary.data.remotes.responses.SignInResponse
import dev.cisnux.dietary.data.remotes.responses.UserAccountBodyRequest
import dev.cisnux.dietary.utils.DIETARY_API
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import dev.cisnux.dietary.utils.Failure
import io.ktor.client.call.body
import io.ktor.client.request.put
import io.ktor.util.network.UnresolvedAddressException

class UserAccountRemoteSourceImpl @Inject constructor(
    private val client: HttpClient
) : UserAccountRemoteSource {
    override suspend fun signIn(userAccount: UserAccountBodyRequest): Either<Exception, String> =
        withContext(Dispatchers.IO) {
            try {
                val response = client.post(
                    urlString = "$DIETARY_API/authentication"
                )
                val failure = Failure.HTTP_FAILURES[response.status]
                return@withContext if (failure != null) {
                    Either.Left(failure)
                } else {
                    val commonResponse: CommonResponse<SignInResponse> = response.body()
                    Either.Right(commonResponse.data.accessToken)
                }
            } catch (e: UnresolvedAddressException) {
                Either.Left(Failure.ConnectionFailure())
            }
        }

    override suspend fun signUp(userAccount: UserAccountBodyRequest): Either<Exception, Nothing?> =
        withContext(Dispatchers.IO) {
            try {
                val response = client.post(
                    urlString = "$DIETARY_API/account"
                )
                val failure = Failure.HTTP_FAILURES[response.status]
                return@withContext if (failure != null) {
                    Either.Left(failure)
                } else {
                    Either.Right(null)
                }
            } catch (e: UnresolvedAddressException) {
                Either.Left(Failure.ConnectionFailure())
            }
        }

    override suspend fun resetPassword(emailAddress: String): Either<Exception, String?> =
        withContext(Dispatchers.IO) {
            try {
                val response = client.post(
                    urlString = "$DIETARY_API/password"
                )
                val failure = Failure.HTTP_FAILURES[response.status]
                return@withContext if (failure != null) {
                    Either.Left(failure)
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
                )
                val failure = Failure.HTTP_FAILURES[response.status]
                return@withContext if (failure != null) {
                    Either.Left(failure)
                } else {
                    val commonResponse: CommonResponse<Nothing> = response.body()
                    Either.Right(commonResponse.message)
                }
            } catch (e: UnresolvedAddressException) {
                Either.Left(Failure.ConnectionFailure())
            }
        }
}