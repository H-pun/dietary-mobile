package dev.cisnux.dietary.data.repositories

import dev.cisnux.dietary.domain.models.UserAccount
import dev.cisnux.dietary.domain.repositories.AuthenticationRepository
import dev.cisnux.dietary.utils.Failure
import dev.cisnux.dietary.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


class AuthenticationRepositoryImpl @Inject constructor() : AuthenticationRepository {
    override fun signInWithEmailAndPassword(userAccount: UserAccount): Flow<UiState<Nothing>> =
        flow {
            emit(UiState.Loading)
            delay(1000L)
//            emit(UiState.Error(error = Failure.BadRequestFailure(message = "email address or password are not valid")))
            emit(UiState.Success())
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
        flow {
            emit(UiState.Loading)
            delay(1000L)
            emit(UiState.Error(error = Failure.BadRequestFailure(message = "email address or password are not valid")))
//            emit(UiState.Success())
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
}