package dev.cisnux.dietary.domain.usecases

import dev.cisnux.dietary.domain.models.UserAccount
import dev.cisnux.dietary.domain.repositories.AuthenticationRepository
import dev.cisnux.dietary.domain.repositories.UserProfileRepository
import dev.cisnux.dietary.utils.AuthenticationState
import dev.cisnux.dietary.utils.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import dev.cisnux.dietary.utils.Failure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

@ExperimentalCoroutinesApi
class AuthenticationInteractor @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val userProfileRepository: UserProfileRepository,
) : AuthenticationUseCase {
    override val accessToken: Flow<String?>
        get() = authenticationRepository.accessToken
    override val userId: Flow<String?>
        get() = authenticationRepository.userId
    override val isAccessTokenAndUserIdExists: Flow<Pair<String, String>?>
        get() = userId.combine(accessToken) { userId, accessToken ->
            Pair(first = userId, second = accessToken)
        }.map {
            if (it.first != null && it.first?.isNotBlank() == true && it.second != null && it.second?.isNotBlank() == true)
                Pair(
                    first = it.first!!,
                    second = it.second!!
                )
            else null
        }.flowOn(Dispatchers.IO)
            .distinctUntilChanged()
    override val authenticationState: Flow<AuthenticationState>
        get() = isAccessTokenAndUserIdExists.flatMapLatest {
            it?.let {
                userProfileRepository.getUserProfile(accessToken = it.second, userId = it.first)
                    .map { uiSate ->
                        when (uiSate) {
                            is UiState.Success -> AuthenticationState.HAS_SIGNED_IN_AND_USER_PROFILE
                            is UiState.Error -> when (uiSate.error) {
                                is Failure.UnauthorizedFailure -> AuthenticationState.HAS_NOT_SIGNED_IN
                                is Failure.NotFoundFailure -> AuthenticationState.HAS_NOT_USER_PROFILE
                                else -> AuthenticationState.UNKNOWN
                            }

                            else -> AuthenticationState.INITIALIZE
                        }
                    }
            } ?: flow { emit(AuthenticationState.HAS_NOT_SIGNED_IN) }
        }

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

    override suspend fun signOut() = authenticationRepository.signOut()
}