package dev.cisnux.dietary.data.remotes.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// The response of food diaries
// on home screen and report screen
@Serializable
data class FoodDiaryResponse(
    val id: String,
    val title: String,
    @SerialName(value = "added_at")
    val addedAt: Long,
    @SerialName(value = "total_food_calories")
    val totalFoodCalories: Float,
    @SerialName(value = "picture_url")
    val pictureUrl: String,
)