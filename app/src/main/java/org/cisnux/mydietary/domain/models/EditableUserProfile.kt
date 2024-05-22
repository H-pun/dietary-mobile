package org.cisnux.mydietary.domain.models

data class EditableUserProfile(
    val id: String = "",
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
