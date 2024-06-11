package org.cisnux.mydietary.data.remotes

import arrow.core.Either
import org.cisnux.mydietary.data.remotes.bodyrequests.ChangePasswordBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.GoogleTokenBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.NewPasswordBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.ResetPasswordBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.UpdateEmailBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.UserAccountBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.VerifyEmailBodyRequest
import org.cisnux.mydietary.data.remotes.responses.AddedUserAccountResponse

interface UserAccountRemoteSource {
    suspend fun signInWithEmailAddressAndPassword(userAccount: UserAccountBodyRequest): Either<Exception, AddedUserAccountResponse>
    suspend fun verifyEmailAddress(accessToken: String, verifyEmailBodyRequest: VerifyEmailBodyRequest): Either<Exception, String>
    suspend fun updateEmail(accessToken: String, updateEmailBodyRequest: UpdateEmailBodyRequest): Either<Exception, String>
    suspend fun changePassword(accessToken: String, changePasswordBodyRequest: ChangePasswordBodyRequest): Either<Exception, String>
    suspend fun signInWithGoogle(googleToken: GoogleTokenBodyRequest): Either<Exception, AddedUserAccountResponse>
    suspend fun signUp(userAccount: UserAccountBodyRequest): Either<Exception, Nothing?>
    suspend fun sendResetPassword(resetPassword: ResetPasswordBodyRequest): Either<Exception, String>
    suspend fun forgotPassword(newPassword: NewPasswordBodyRequest): Either<Exception, String>
}