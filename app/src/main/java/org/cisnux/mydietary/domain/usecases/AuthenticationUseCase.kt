package org.cisnux.mydietary.domain.usecases

import org.cisnux.mydietary.domain.models.UserAccount
import org.cisnux.mydietary.utils.AuthenticationState
import org.cisnux.mydietary.utils.UiState
import kotlinx.coroutines.flow.Flow

interface AuthenticationUseCase {
    val accessToken: Flow<String?>
    val userId: Flow<String?>
    val isAccessTokenAndUserIdExists: Flow<Pair<String, String>?>
    val authenticationState: Flow<AuthenticationState>
    fun signInWithEmailAndPassword(userAccount: UserAccount): Flow<UiState<Nothing>>
    fun signInWithGoogle(token: String): Flow<UiState<Nothing>>
    fun signUpWithEmailAndPassword(userAccount: UserAccount): Flow<UiState<Nothing>>
    fun signUpWithGoogle(): Flow<UiState<Nothing>>
    fun resetPassword(emailAddress: String): Flow<UiState<Nothing>>
    suspend fun signOut()
}
