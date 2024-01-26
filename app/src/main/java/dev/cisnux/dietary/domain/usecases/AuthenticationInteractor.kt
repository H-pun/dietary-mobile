package dev.cisnux.dietary.domain.usecases

import dev.cisnux.dietary.domain.models.UserAccount
import dev.cisnux.dietary.domain.repositories.AuthenticationRepository
import dev.cisnux.dietary.domain.repositories.TokenRepository
import dev.cisnux.dietary.utils.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
class AuthenticationInteractor @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val authenticationRepository: AuthenticationRepository
) : AuthenticationUseCase {
    override val hasAuthTokenExpired: Flow<Boolean>
        get() = tokenRepository.hasAuthTokenExpired

    override val hasFoodSecretTokenExpired: Flow<Boolean>
        get() = tokenRepository.hasFoodSecretTokenExpired

    override fun signInWithEmailAndPassword(userAccount: UserAccount): Flow<UiState<Nothing>> =
        authenticationRepository.signInWithEmailAndPassword(userAccount)

    override fun signInWithGoogle(): Flow<UiState<Nothing>> =
        authenticationRepository.signInWithGoogle()

    override fun signUpWithEmailAndPassword(userAccount: UserAccount): Flow<UiState<Nothing>> =
        authenticationRepository.signUpWithEmailAndPassword(userAccount)

    override fun signUpWithGoogle(): Flow<UiState<Nothing>> =
        authenticationRepository.signUpWithGoogle()

    override fun resetPassword(emailAddress: String): Flow<UiState<Nothing>> =
        authenticationRepository.resetPassword(emailAddress)
}