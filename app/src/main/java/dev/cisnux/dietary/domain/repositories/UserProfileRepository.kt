package dev.cisnux.dietary.domain.repositories

import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    val isUserProfileExist: Flow<Boolean>
}
