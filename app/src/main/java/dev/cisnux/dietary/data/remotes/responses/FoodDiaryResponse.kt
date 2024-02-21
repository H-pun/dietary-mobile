package dev.cisnux.dietary.data.remotes.responses

import kotlinx.serialization.Serializable

// The response of food diaries
// on home screen and report screen
@Serializable
data class FoodDiaryResponse(
    val id: String,
    val title: String,
    val addedAt: String,
    val totalFoodCalories: Float,
    val filePath: String,
)