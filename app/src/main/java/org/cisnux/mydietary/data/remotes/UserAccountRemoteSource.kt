package org.cisnux.mydietary.data.remotes

import arrow.core.Either
import org.cisnux.mydietary.data.remotes.bodyrequests.ChangePasswordBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.GoogleTokenRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.NewPasswordBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.ResetPasswordBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.UpdateEmailBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.UserAccountBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.VerifyEmailBodyRequest
import org.cisnux.mydietary.data.remotes.responses.AddedUserAccountResponse

interface UserAccountRemoteSource {
    suspend fun signInUserAccount(userAccount: UserAccountBodyRequest): Either<Exception, AddedUserAccountResponse>
    suspend fun verifyEmailAddress(accessToken: String, verifyEmailBodyRequest: VerifyEmailBodyRequest): Either<Exception, String>
    suspend fun updateEmail(accessToken: String, updateEmailBodyRequest: UpdateEmailBodyRequest): Either<Exception, String>
    suspend fun updatePassword(accessToken: String, changePasswordBodyRequest: ChangePasswordBodyRequest): Either<Exception, String>
    suspend fun verifyGoogleAccount(googleToken: GoogleTokenRequest): Either<Exception, AddedUserAccountResponse>
    suspend fun addUserAccount(userAccount: UserAccountBodyRequest): Either<Exception, Nothing?>
    suspend fun resetPassword(resetPassword: ResetPasswordBodyRequest): Either<Exception, String>
    suspend fun updatePassword(newPassword: NewPasswordBodyRequest): Either<Exception, String>
}