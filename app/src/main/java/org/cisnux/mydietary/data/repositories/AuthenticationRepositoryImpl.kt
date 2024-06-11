package org.cisnux.mydietary.data.repositories

import org.cisnux.mydietary.data.locals.UserAccountLocalSource
import org.cisnux.mydietary.data.remotes.UserAccountRemoteSource
import org.cisnux.mydietary.domain.models.UserAccount
import org.cisnux.mydietary.domain.repositories.AuthenticationRepository
import org.cisnux.mydietary.utils.UiState
import org.cisnux.mydietary.utils.userAccountBodyRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.cisnux.mydietary.data.remotes.bodyrequests.ChangePasswordBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.GoogleTokenBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.NewPasswordBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.ResetPasswordBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.UpdateEmailBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.VerifyEmailBodyRequest
import org.cisnux.mydietary.domain.models.ChangePassword
import org.cisnux.mydietary.domain.models.ForgotPassword
import javax.inject.Inject


class AuthenticationRepositoryImpl @Inject constructor(
    private val userAccountRemoteSource: UserAccountRemoteSource,
    private val userAccountLocalSource: UserAccountLocalSource,
) : AuthenticationRepository {
    override val accessToken: Flow<String?>
        get() = userAccountLocalSource.accessToken
    override val userId: Flow<String?>
        get() = userAccountLocalSource.userId

    override fun signInWithEmailAddressAndPassword(userAccount: UserAccount): Flow<UiState<Nothing>> =
        flow {
            emit(UiState.Loading)
            userAccountRemoteSource.signInWithEmailAddressAndPassword(userAccount.userAccountBodyRequest)
                .fold(
                    ifLeft = {
                        emit(UiState.Error(it))
                    },
                    ifRight = {
                        userAccountLocalSource.updateUserId(it.id)
                        userAccountLocalSource.updateAccessToken(it.accessToken)
                        emit(UiState.Success(null))
                    }
                )
        }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)

    override fun signUp(userAccount: UserAccount): Flow<UiState<Nothing>> =
        channelFlow {
            send(UiState.Loading)
            userAccountRemoteSource.signUp(userAccount.userAccountBodyRequest)
                .fold(
                    ifLeft = {
                        send(UiState.Error(it))
                    },
                    ifRight = {
                        send(UiState.Success(null))
                    }
                )
        }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)

    override suspend fun signInWithGoogle(token: String): Flow<UiState<Nothing>> = flow {
        emit(UiState.Loading)
        userAccountRemoteSource.signInWithGoogle(googleToken = GoogleTokenBodyRequest(idToken = token))
            .fold(
                ifLeft = {
                    emit(UiState.Error(it))
                },
                ifRight = {
                    userAccountLocalSource.updateUserId(it.id)
                    userAccountLocalSource.updateAccessToken(it.accessToken)
                    emit(UiState.Success())
                }
            )
    }

    override fun sendResetPassword(emailAddress: String): Flow<UiState<String>> =
        flow {
            emit(UiState.Loading)
            userAccountRemoteSource.sendResetPassword(
                resetPassword = ResetPasswordBodyRequest(
                    emailAddress = emailAddress
                )
            ).fold(
                ifLeft = { exception ->
                    emit(UiState.Error(exception))
                },
                ifRight = {
                    emit(UiState.Success(it))
                }
            )
        }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)

    override fun forgotPassword(forgotPassword: ForgotPassword): Flow<UiState<String>> = flow {
        emit(UiState.Loading)
        userAccountRemoteSource.forgotPassword(
            newPassword = NewPasswordBodyRequest(
                code = forgotPassword.code,
                newPassword = forgotPassword.newPassword,
                emailAddress = forgotPassword.emailAddress
            )
        )
            .fold(
                ifLeft = { exception ->
                    emit(UiState.Error(exception))
                },
                ifRight = {
                    emit(UiState.Success(it))
                })
    }.distinctUntilChanged()
        .flowOn(Dispatchers.IO)

    override fun changePassword(
        accessToken: String,
        id: String,
        changePassword: ChangePassword
    ): Flow<UiState<String>> = flow {
        emit(UiState.Loading)
        userAccountRemoteSource.changePassword(
            accessToken = accessToken,
            changePasswordBodyRequest = ChangePasswordBodyRequest(
                id = id,
                oldPassword = changePassword.oldPassword,
                newPassword = changePassword.newPassword
            )
        )
            .fold(
                ifLeft = { exception ->
                    emit(UiState.Error(exception))
                },
                ifRight = {
                    emit(UiState.Success(it))
                })
    }
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)

    override fun changeEmail(
        accessToken: String,
        id: String,
        email: String
    ): Flow<UiState<String>> = flow {
        emit(UiState.Loading)
        userAccountRemoteSource.updateEmail(
            accessToken = accessToken,
            updateEmailBodyRequest = UpdateEmailBodyRequest(
                id = id,
                emailAddress = email
            )
        )
            .fold(
                ifLeft = { exception ->
                    emit(UiState.Error(exception))
                },
                ifRight = {
                    emit(UiState.Success(it))
                })
    }.distinctUntilChanged()
        .flowOn(Dispatchers.IO)

    override fun verifyEmail(accessToken: String, email: String): Flow<UiState<String>> =
        flow {
            emit(UiState.Loading)
            userAccountRemoteSource.verifyEmailAddress(
                accessToken = accessToken,
                verifyEmailBodyRequest = VerifyEmailBodyRequest(emailAddress = email)
            )
                .fold(
                    ifLeft = { exception ->
                        emit(UiState.Error(exception))
                    },
                    ifRight = {
                        emit(UiState.Success(it))
                    }
                )
        }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)

    override suspend fun deleteSession() = withContext(Dispatchers.IO) {
        userAccountLocalSource.updateAccessToken("")
        userAccountLocalSource.updateUserId("")
    }
}