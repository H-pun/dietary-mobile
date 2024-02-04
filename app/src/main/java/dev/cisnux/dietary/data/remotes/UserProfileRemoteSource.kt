package dev.cisnux.dietary.data.remotes

import arrow.core.Either
import dev.cisnux.dietary.data.remotes.responses.UserProfileBodyRequest
import dev.cisnux.dietary.data.remotes.responses.UserProfileDetailResponse

interface UserProfileRemoteSource {
    suspend fun addUserProfile(accessToken: String, userProfile: UserProfileBodyRequest): Either<Exception, Nothing?>
    suspend fun updateUserProfile(accessToken: String, userProfile: UserProfileBodyRequest): Either<Exception, Nothing?>
    suspend fun getUserProfile(accessToken: String): Either<Exception, UserProfileDetailResponse>
}