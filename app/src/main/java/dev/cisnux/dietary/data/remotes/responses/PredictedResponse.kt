package dev.cisnux.dietary.data.remotes.responses

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class PredictedResponse(
    val foods: List<PredictedFoodResponse>,
    @SerialName("imagePlotPath")
    val imagePath: String,
    val totalCalories: Float,
    val totalProtein: Float,
    val totalFat: Float,
    val totalCarbohydrate: Float,
)
