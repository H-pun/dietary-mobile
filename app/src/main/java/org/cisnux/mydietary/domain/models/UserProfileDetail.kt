package org.cisnux.mydietary.domain.models

data class UserProfileDetail(
    val id: String,
    val userAccountId: String,
    val username: String,
    val emailAddress: String,
    val age: Int,
    val weight: Float,
    val height: Float,
    val gender: String,
    val goal: String,
    val weightTarget: Float = 0f,
    val activityLevel: String,
)
