package org.cisnux.mydietary.domain.repositories

import org.cisnux.mydietary.domain.models.UserAccount
import org.cisnux.mydietary.utils.UiState
import kotlinx.coroutines.flow.Flow
import org.cisnux.mydietary.domain.models.ChangePassword
import org.cisnux.mydietary.domain.models.ForgotPassword

interface AuthenticationRepository {
    val accessToken: Flow<String?>
    val userId: Flow<String?>
    fun verifyUserAccount(userAccount: UserAccount): Flow<UiState<Nothing>>
    fun addUserAccount(userAccount: UserAccount): Flow<UiState<Nothing>>
    suspend fun verifyGoogleAccount(token: String): Flow<UiState<Nothing>>
    fun resetPassword(emailAddress: String): Flow<UiState<String>>
    fun updatePassword(forgotPassword: ForgotPassword): Flow<UiState<String>>
    fun changePassword(accessToken: String, id: String, changePassword: ChangePassword): Flow<UiState<String>>
    fun changeEmail(accessToken: String, id: String, email: String): Flow<UiState<String>>
    fun verifyEmail(accessToken: String, email: String): Flow<UiState<String>>
    suspend fun deleteSession()
}
