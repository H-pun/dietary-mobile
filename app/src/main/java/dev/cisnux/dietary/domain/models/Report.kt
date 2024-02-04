package dev.cisnux.dietary.domain.models

data class Report(
    val totalUserCaloriesToday: Float,
    val maxDailyBmiCalorie: Float,
    val foods: List<FoodDiaryReport>
)
