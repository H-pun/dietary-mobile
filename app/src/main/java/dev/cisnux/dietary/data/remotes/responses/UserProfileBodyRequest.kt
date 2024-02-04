package dev.cisnux.dietary.data.remotes.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The body request to add a new user profile
 * or update user profile.
 * */
@Serializable
data class UserProfileBodyRequest(
    val username: String,
    val age: Int,
    val weight: Float,
    val height: Float,
    val gender: String,
    val goal: String,
    @SerialName(value = "weight_target")
    val weightTarget: Float = 0f,
    @SerialName(value = "activity_level")
    val activityLevel: String
)