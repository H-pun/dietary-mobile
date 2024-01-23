package dev.cisnux.dietary.domain.usecases

import dev.cisnux.dietary.domain.repositories.TokenRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
class AuthenticationInteractor @Inject constructor(
    private val tokenRepository: TokenRepository
) : AuthenticationUseCase {
    override val hasAuthTokenExpired: Flow<Boolean>
        get() = tokenRepository.hasAuthTokenExpired

    override val hasFoodSecretTokenExpired: Flow<Boolean>
        get() = tokenRepository.hasFoodSecretTokenExpired
}