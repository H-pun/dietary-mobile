package dev.cisnux.dietary.data.remotes.responses

import kotlinx.serialization.Serializable

// The response of food diary detail
@Serializable
data class FoodDiaryDetailResponse(
    val id: String,
    val title: String,
    val status: String?,
    val filePath: String,
    val feedback: List<String>,
    val totalCalories: Float,
    val totalProtein: Float,
    val totalFat: Float,
    val totalCarbohydrate: Float,
    // the foods which was detected
    val foods: List<PredictedFoodDetailResponse>,
)