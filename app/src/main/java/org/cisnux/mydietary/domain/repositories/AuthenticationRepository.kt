package org.cisnux.mydietary.domain.repositories

import org.cisnux.mydietary.domain.models.UserAccount
import org.cisnux.mydietary.utils.UiState
import kotlinx.coroutines.flow.Flow
import org.cisnux.mydietary.domain.models.ChangePassword

interface AuthenticationRepository {
    val accessToken: Flow<String?>
    val userId: Flow<String?>
    fun verifyUserAccount(userAccount: UserAccount): Flow<UiState<Nothing>>
    fun addUserAccount(userAccount: UserAccount): Flow<UiState<Nothing>>
    suspend fun verifyGoogleAccount(token: String): Flow<UiState<Nothing>>
    fun resetPassword(emailAddress: String): Flow<UiState<String>>
    fun updatePassword(changePassword: ChangePassword): Flow<UiState<String>>
    suspend fun deleteSession()
}
