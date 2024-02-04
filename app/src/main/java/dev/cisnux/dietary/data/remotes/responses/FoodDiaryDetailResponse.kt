package dev.cisnux.dietary.data.remotes.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// The response of food diary detail
@Serializable
data class FoodDiaryDetailResponse(
    val id: String,
    @SerialName(value = "total_user_calories_today")
    val totalUserCaloriesToday: Float,
    @SerialName(value = "max_daily_bmr_calories")
    val maxDailyBmrCalorie: Float,
    @SerialName(value = "total_food_calories")
    val totalFoodCalories: Float,
    val status: String,
    @SerialName(value = "food_picture_url")
    val foodPictureUrl: String,
    val feedback: String? = null,
    // the foods which was detected
    val foods: List<FoodResponse>,
)