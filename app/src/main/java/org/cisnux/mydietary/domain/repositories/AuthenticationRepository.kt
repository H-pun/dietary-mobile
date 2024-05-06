package org.cisnux.mydietary.domain.repositories

import org.cisnux.mydietary.domain.models.UserAccount
import org.cisnux.mydietary.utils.UiState
import kotlinx.coroutines.flow.Flow

interface AuthenticationRepository {
    val accessToken: Flow<String?>
    val userId: Flow<String?>
    fun signInWithEmailAndPassword(userAccount: UserAccount): Flow<UiState<Nothing>>
    fun signInWithGoogle(): Flow<UiState<Nothing>>
    fun signUpWithEmailAndPassword(userAccount: UserAccount): Flow<UiState<Nothing>>
    fun signUpWithGoogle(): Flow<UiState<Nothing>>
    fun resetPassword(emailAddress: String): Flow<UiState<Nothing>>
    suspend fun signOut()
}
