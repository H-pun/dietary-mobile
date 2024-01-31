package dev.cisnux.dietary.domain.models

data class FoodDiaryResult(
    val foodDiaryId: String,
    val totalFoodCalories: Float,
    val userDailyBmiCalorie: Float,
    val foods: List<Food>,
    val questions: List<Question>
)
