package org.cisnux.mydietary.data.remotes

import arrow.core.Either
import org.cisnux.mydietary.data.remotes.bodyrequests.NewUserProfileBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.UpdateUserProfileBodyRequest
import org.cisnux.mydietary.data.remotes.responses.NutrientResponse
import org.cisnux.mydietary.data.remotes.responses.UserProfileDetailResponse

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