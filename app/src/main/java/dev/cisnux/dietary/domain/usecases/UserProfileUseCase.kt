package dev.cisnux.dietary.domain.usecases

import dev.cisnux.dietary.domain.models.UserNutrition
import dev.cisnux.dietary.domain.models.UserProfile
import dev.cisnux.dietary.domain.models.UserProfileDetail
import dev.cisnux.dietary.utils.UiState
import kotlinx.coroutines.flow.Flow

interface UserProfileUseCase {
    val isUserProfileExist: Flow<Boolean>
    val userProfileDetail: Flow<UserProfileDetail>
    val userDailyNutrition: Flow<UiState<UserNutrition>>
    fun addUserProfile(userProfile: UserProfile): Flow<UiState<Nothing>>
    fun updateUserProfile(userProfile: UserProfile): Flow<UiState<Nothing>>
    fun refreshUserProfile(): Flow<UiState<Nothing>>
}