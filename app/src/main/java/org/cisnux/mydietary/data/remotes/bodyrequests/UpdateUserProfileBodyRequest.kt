package org.cisnux.mydietary.data.remotes.bodyrequests

import kotlinx.serialization.Serializable

@Serializable
sealed interface UpdateUserProfileBodyRequest {
    val id: String
    val age: Int
    val weight: Float
    val height: Float
    val waistCircumference: Float
    val gender: String
    val goal: String
    val weightTarget: Float
    val activityLevel: String
}