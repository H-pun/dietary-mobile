package org.cisnux.mydietary.domain.usecases

import android.app.Activity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.cisnux.mydietary.domain.models.ChangePassword
import org.cisnux.mydietary.domain.models.ForgotPassword
import org.cisnux.mydietary.domain.models.UserAccount
import org.cisnux.mydietary.utils.AuthenticationState
import org.cisnux.mydietary.utils.UiState

interface AuthenticationUseCase {
    fun getAuthenticationState(scope: CoroutineScope): Flow<AuthenticationState>
    fun getAccessTokenAndUserId(scope: CoroutineScope): Flow<Pair<String, String>?>
    fun getAccessToken(scope: CoroutineScope): Flow<String?>
    fun getUserId(scope: CoroutineScope): Flow<String?>
    fun signInWithEmailAndPassword(userAccount: UserAccount, scope: CoroutineScope): Flow<UiState<Nothing>>
    fun signInWithGoogle(scope: CoroutineScope, context: Activity): Flow<UiState<Nothing>>
    fun signUpWithEmailAndPassword(userAccount: UserAccount, scope: CoroutineScope): Flow<UiState<Nothing>>
    fun sendResetPassword(emailAddress: String, scope: CoroutineScope): Flow<UiState<String>>
    fun forgotPassword(forgotPassword: ForgotPassword, scope: CoroutineScope): Flow<UiState<String>>
    fun changePassword(changePassword: ChangePassword, scope: CoroutineScope): Flow<UiState<String>>
    fun changeEmail(newEmail: String, scope: CoroutineScope): Flow<UiState<String>>
    fun verifyEmail(email: String, scope: CoroutineScope): Flow<UiState<String>>
    suspend fun signOut()
}
