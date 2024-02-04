package dev.cisnux.dietary.data.remotes

import arrow.core.Either
import dev.cisnux.dietary.data.remotes.responses.NewPasswordBodyRequest
import dev.cisnux.dietary.data.remotes.responses.UserAccountBodyRequest

interface UserAccountRemoteSource {
    suspend fun signIn(userAccount: UserAccountBodyRequest): Either<Exception, String>
    suspend fun signUp(userAccount: UserAccountBodyRequest): Either<Exception, Nothing?>
    suspend fun resetPassword(emailAddress: String): Either<Exception, String?>
    suspend fun newPassword(newPassword: NewPasswordBodyRequest): Either<Exception, String?>
}