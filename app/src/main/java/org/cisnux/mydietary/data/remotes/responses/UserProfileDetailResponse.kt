package org.cisnux.mydietary.data.remotes.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// The response of user profile detail
@Serializable
data class UserProfileDetailResponse(
    @SerialName("idUser")
    val userAccountId: String,
    val id: String,
    val username: String,
    @SerialName("email")
    val emailAddress: String,
    val age: Int,
    val weight: Float,
    val height: Float,
    val waistCircumference: Float,
    val gender: String,
    val goal: String,
    val weightTarget: Float = 0f,
    val activityLevel: String,
    val isVerified: Boolean = false
)
