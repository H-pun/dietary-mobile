package dev.cisnux.dietary.domain.models

data class FoodScannerReport(
    val reportId: String,
    val totalFoodCalories: Float,
    val userDailyBmiCalorie: Float,
    val foods: List<Food>
)
