package org.cisnux.mydietary.data.remotes

import arrow.core.Either
import org.cisnux.mydietary.data.remotes.bodyrequests.DietProgressBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.NewUserProfileBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.UpdateUserProfileBodyRequest
import org.cisnux.mydietary.data.remotes.responses.DietProgressResponse
import org.cisnux.mydietary.data.remotes.responses.NutrientResponse
import org.cisnux.mydietary.data.remotes.responses.UserProfileDetailResponse

interface UserProfileRemoteSource {
    suspend fun addUserProfile(accessToken: String, userProfile: NewUserProfileBodyRequest): Either<Exception, String>
    suspend fun updateUserProfile(accessToken: String, userProfile: UpdateUserProfileBodyRequest): Either<Exception, Nothing?>
    suspend fun getUserProfile(accessToken: String, userAccountId: String): Either<Exception, UserProfileDetailResponse>
    suspend fun getDailyNutrients(
        accessToken: String,
        userId: String,
        date: String
    ): Either<Exception, NutrientResponse>

    suspend fun updateDietProgress(accessToken: String, dietProgressBodyRequest: DietProgressBodyRequest): Either<Exception, Nothing?>
    suspend fun getDietProgress(accessToken: String, userAccountId: String): Either<Exception, List<DietProgressResponse>>
}