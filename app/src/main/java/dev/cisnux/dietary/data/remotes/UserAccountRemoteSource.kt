package dev.cisnux.dietary.data.remotes

import arrow.core.Either
import dev.cisnux.dietary.data.remotes.bodyrequests.NewPasswordBodyRequest
import dev.cisnux.dietary.data.remotes.bodyrequests.ResetPasswordBodyRequest
import dev.cisnux.dietary.data.remotes.bodyrequests.UserAccountBodyRequest

interface UserAccountRemoteSource {
    suspend fun signIn(userAccount: UserAccountBodyRequest): Either<Exception, String>
    suspend fun signUp(userAccount: UserAccountBodyRequest): Either<Exception, Nothing?>
    suspend fun resetPassword(resetPassword: ResetPasswordBodyRequest): Either<Exception, String?>
    suspend fun newPassword(newPassword: NewPasswordBodyRequest): Either<Exception, String?>
}