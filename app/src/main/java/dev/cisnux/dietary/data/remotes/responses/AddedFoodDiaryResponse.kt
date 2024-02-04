package dev.cisnux.dietary.data.remotes.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The response of added food diary
 * or after user answered the questions
 * then food diary get updated.
 * */
@Serializable
data class AddedFoodDiaryResponse(
    val id: String,
    @SerialName(value = "total_user_calories_today")
    val totalUserCaloriesToday: Float,
    @SerialName(value = "max_daily_bmi_calories")
    val maxDailyBmiCalorie: Float,
    @SerialName(value = "total_food_calories")
    val totalFoodCalories: Float,
    val status: String,
    val feedback: String? = null,
    // the foods which was detected
    val foods: List<DetectedFoodResponse>,
)