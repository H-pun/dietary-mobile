package dev.cisnux.dietary.domain.usecases

import kotlinx.coroutines.flow.Flow

interface UserProfileUseCase {
    val isUserProfileExist: Flow<Boolean>
}