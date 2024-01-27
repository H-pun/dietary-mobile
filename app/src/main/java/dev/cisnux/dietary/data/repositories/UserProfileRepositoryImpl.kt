package dev.cisnux.dietary.data.repositories

import dev.cisnux.dietary.domain.models.UserProfile
import dev.cisnux.dietary.domain.repositories.UserProfileRepository
import dev.cisnux.dietary.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UserProfileRepositoryImpl @Inject constructor() : UserProfileRepository {
    override fun addUserProfile(userProfile: UserProfile): Flow<UiState<Nothing>> =
        flow {
            emit(UiState.Loading)
            delay(1000L)
//            emit(UiState.Error(error = Failure.BadRequestFailure(message = "email address or password are not valid")))
            emit(UiState.Success())
        }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)

    override val isUserProfileExist: Flow<Boolean>
        get() = flow {
            emit(true)
        }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)
}