package org.cisnux.mydietary.data.remotes.bodyrequests

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserProfileWithUsernameBodyRequest(
    override val id: String,
    val username: String,
    override val age: Int,
    override val weight: Float,
    override val height: Float,
    override val waistCircumference: Float,
    override val gender: String,
    override val goal: String,
    override val weightTarget: Float = 0f,
    override val activityLevel: String
) : UpdateUserProfileBodyRequest
