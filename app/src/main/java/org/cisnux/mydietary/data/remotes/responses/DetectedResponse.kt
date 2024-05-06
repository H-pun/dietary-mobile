package org.cisnux.mydietary.data.remotes.responses

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class DetectedResponse(
    val foods: List<DetectedFoodResponse>,
    @SerialName("imagePlotPath")
    val imagePath: String,
    val totalCalories: Float,
    val totalProtein: Float,
    val totalFat: Float,
    val totalCarbohydrate: Float,
)
