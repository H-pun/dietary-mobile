package dev.cisnux.dietary.data.remotes.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// The response of user profile detail
@Serializable
data class UserProfileDetailResponse(
    val username: String,
    @SerialName(value = "email_address")
    val emailAddress: String,
    val age: Int,
    val weight: Float,
    val height: Float,
    val gender: String,
    val goal: String,
    @SerialName(value = "weight_target")
    val weightTarget: Float = 0f,
    @SerialName(value = "activity_level")
    val activityLevel: String,
)
