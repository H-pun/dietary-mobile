package org.cisnux.mydietary.data.remotes

import arrow.core.Either
import org.cisnux.mydietary.data.remotes.bodyrequests.NewPasswordBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.ResetPasswordBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.UserAccountBodyRequest
import org.cisnux.mydietary.data.remotes.responses.AddedUserAccountResponse

interface UserAccountRemoteSource {
    suspend fun signIn(userAccount: UserAccountBodyRequest): Either<Exception, AddedUserAccountResponse>
    suspend fun signUp(userAccount: UserAccountBodyRequest): Either<Exception, Nothing?>
    suspend fun resetPassword(resetPassword: ResetPasswordBodyRequest): Either<Exception, String?>
    suspend fun newPassword(newPassword: NewPasswordBodyRequest): Either<Exception, String?>
}