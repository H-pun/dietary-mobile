package dev.cisnux.dietary.data.repositories

import dev.cisnux.dietary.data.locals.UserAccountLocalSource
import dev.cisnux.dietary.data.remotes.UserAccountRemoteSource
import dev.cisnux.dietary.domain.models.UserAccount
import dev.cisnux.dietary.domain.repositories.AuthenticationRepository
import dev.cisnux.dietary.utils.Failure
import dev.cisnux.dietary.utils.UiState
import dev.cisnux.dietary.utils.userAccountBodyRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject


class AuthenticationRepositoryImpl @Inject constructor(
    private val userAccountRemoteSource: UserAccountRemoteSource,
    private val userAccountLocalSource: UserAccountLocalSource,
) : AuthenticationRepository {
    override val accessToken: Flow<String?>
        get() = userAccountLocalSource.accessToken
    override val userId: Flow<String?>
        get() = userAccountLocalSource.userId

    override fun signInWithEmailAndPassword(userAccount: UserAccount): Flow<UiState<Nothing>> =
        channelFlow {
            send(UiState.Loading)
            delay(1000L)
            userAccountRemoteSource.signIn(userAccount.userAccountBodyRequest)
                .fold(
                    ifLeft = {
                        send(UiState.Error(it))
                    },
                    ifRight = {
                        userAccountLocalSource.updateUserId(it.id)
                        userAccountLocalSource.updateAccessToken(it.accessToken)
                        send(UiState.Success(null))
                    }
                )
        }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)

    override fun signInWithGoogle(): Flow<UiState<Nothing>> =
        flow {
            emit(UiState.Loading)
            delay(1000L)
            emit(UiState.Error(error = Failure.BadRequestFailure(message = "email address or password are not valid")))
//            emit(UiState.Success())
        }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)

    override fun signUpWithEmailAndPassword(userAccount: UserAccount): Flow<UiState<Nothing>> =
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

    override fun signUpWithGoogle(): Flow<UiState<Nothing>> =
        flow {
            emit(UiState.Loading)
            delay(1000L)
//            emit(UiState.Error(error = Failure.BadRequestFailure(message = "email address or password are not valid")))
            emit(UiState.Success())
        }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)

    override fun resetPassword(emailAddress: String): Flow<UiState<Nothing>> =
        flow {
            emit(UiState.Loading)
            delay(1000L)
//            emit(UiState.Error(error = Failure.BadRequestFailure(message = "email address or password are not valid")))
            emit(UiState.Success())
        }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)

    override suspend fun signOut() = withContext(Dispatchers.IO) {
        userAccountLocalSource.updateAccessToken("")
        userAccountLocalSource.updateUserId("")
    }
}