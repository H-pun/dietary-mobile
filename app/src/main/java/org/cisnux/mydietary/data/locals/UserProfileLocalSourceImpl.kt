package org.cisnux.mydietary.data.locals

import androidx.datastore.core.DataStore
import org.cisnux.mydietary.UserProfile
import org.cisnux.mydietary.data.remotes.responses.UserProfileDetailResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserProfileLocalSourceImpl @Inject constructor(
    private val dataStore: DataStore<UserProfile>
) : UserProfileLocalSource {
    override val userProfile: Flow<UserProfile>
        get() = dataStore.data

    override suspend fun updateUserProfile(userProfileDetailResponse: UserProfileDetailResponse) {
        dataStore.updateData { userProfile ->
            userProfile
                .toBuilder()
                .setUserAccountId(userProfileDetailResponse.userAccountId)
                .setId(userProfileDetailResponse.id)
                .setUsername(userProfileDetailResponse.username)
                .setEmailAddress(userProfileDetailResponse.emailAddress)
                .setAge(userProfileDetailResponse.age)
                .setWeight(userProfileDetailResponse.weight)
                .setHeight(userProfileDetailResponse.height)
                .setGender(userProfileDetailResponse.gender)
                .setGoal(userProfileDetailResponse.goal)
                .setWeightTarget(userProfileDetailResponse.weightTarget)
                .setActivityLevel(userProfileDetailResponse.activityLevel)
                .setWaistCircumference(userProfileDetailResponse.waistCircumference)
                .setIsVerified(userProfileDetailResponse.isVerified)
                .build()
        }
    }

    override suspend fun delete() {
        dataStore.updateData { userProfile ->
            userProfile.toBuilder().clear().build()
        }
    }
}