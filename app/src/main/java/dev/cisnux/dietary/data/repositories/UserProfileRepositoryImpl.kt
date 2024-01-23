package dev.cisnux.dietary.data.repositories

import dev.cisnux.dietary.domain.repositories.UserProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UserProfileRepositoryImpl @Inject constructor() : UserProfileRepository {
    override val isUserProfileExist: Flow<Boolean>
        get() = flow {
            emit(true)
        }
            .flowOn(Dispatchers.IO)
}