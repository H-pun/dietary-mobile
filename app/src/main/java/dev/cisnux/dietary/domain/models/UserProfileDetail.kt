package dev.cisnux.dietary.domain.models

data class UserProfileDetail(
    val username: String,
    val emailAddress: String,
    val age: Int,
    val weight: Float,
    val height: Float,
    val gender: String,
    val goal: String,
    val targetWeight: Float = 0F,
    val activity: String
)
