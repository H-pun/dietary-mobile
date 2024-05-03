package dev.cisnux.dietary.data.remotes

import arrow.core.Either
import dev.cisnux.dietary.data.remotes.bodyrequests.NewUserProfileBodyRequest
import dev.cisnux.dietary.data.remotes.bodyrequests.UpdateUserProfileBodyRequest
import dev.cisnux.dietary.data.remotes.responses.NutrientResponse
import dev.cisnux.dietary.data.remotes.responses.UserProfileDetailResponse

interface UserProfileRemoteSource {
    suspend fun addUserProfile(accessToken: String, userProfile: NewUserProfileBodyRequest): Either<Exception, Nothing?>
    suspend fun updateUserProfile(accessToken: String, userProfile: UpdateUserProfileBodyRequest): Either<Exception, Nothing?>
    suspend fun getUserProfile(accessToken: String, userAccountId: String): Either<Exception, UserProfileDetailResponse>
    suspend fun getDailyNutrients(
        accessToken: String,
        userId: String,
        date: String
    ): Either<Exception, NutrientResponse>
}