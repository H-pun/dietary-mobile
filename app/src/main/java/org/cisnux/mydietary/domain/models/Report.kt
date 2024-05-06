package org.cisnux.mydietary.domain.models

data class Report(
    val totalUserCaloriesToday: Float,
    val maxDailyBmiCalorie: Float,
    val foods: List<FoodDiaryReport>
)
