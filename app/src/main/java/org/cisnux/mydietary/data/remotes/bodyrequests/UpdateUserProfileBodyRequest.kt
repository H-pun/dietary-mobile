package org.cisnux.mydietary.data.remotes.bodyrequests

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserProfileBodyRequest(
    val id: String,
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