package dev.cisnux.dietary.domain.models

data class FoodDiaryDetail(
    val foodDiaryId: String,
    val totalUserCaloriesToday: Float,
    val totalFoodCalories: Float,
    val maxDailyBmiCalorie: Float,
    val status: String,
    val foodPicture: String? = null,
    val feedback: String?,
    val foods: List<Food>,
)