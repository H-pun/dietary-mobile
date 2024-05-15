package org.cisnux.mydietary.data.remotes.bodyrequests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The body request to add a new user profile
 * or update user profile.
 * */
@Serializable
data class NewUserProfileBodyRequest(
    @SerialName("idUser")
    val userAccountId: String,
    val username: String,
    val age: Int,
    val weight: Float,
    val height: Float,
    val waistCircumference: Float,
    val gender: String,
    val goal: String,
    val weightTarget: Float = 0f,
    val activityLevel: String
)