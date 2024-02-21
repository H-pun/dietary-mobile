package dev.cisnux.dietary.data.remotes.responses

import kotlinx.serialization.Serializable

// The response of food diary detail
@Serializable
data class FoodDiaryDetailResponse(
    val id: String,
    val totalUserCaloriesToday: Float,
    val maxDailyBmrCalorie: Float,
    val totalFoodCalories: Float,
    val status: String,
    val foodPictureUrl: String,
    val feedback: String? = null,
    // the foods which was detected
    val foods: List<FoodResponse>,
)