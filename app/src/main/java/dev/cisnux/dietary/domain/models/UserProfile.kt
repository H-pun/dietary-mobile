package dev.cisnux.dietary.domain.models

data class UserProfile(
    val id: String = "",
    val username: String,
    val age: Int,
    val weight: Float,
    val height: Float,
    val gender: String,
    val goal: String,
    val weightTarget: Float = 0f,
    val activityLevel: String
)
