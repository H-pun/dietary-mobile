package dev.cisnux.dietary.data.remotes.responses

import kotlinx.serialization.Serializable

/**
 * The response of added food diary
 * or after user answered the questions
 * then food diary get updated.
 * */
@Serializable
data class AddedFoodDiaryResponse(
    val id: String,
    val totalUserCaloriesToday: Float,
    val maxDailyBmrCalorie: Float,
    val totalFoodCalories: Float,
    val status: String,
    val feedback: String? = null,
    // the foods which was detected
    val foods: List<DetectedFoodResponse>,
)