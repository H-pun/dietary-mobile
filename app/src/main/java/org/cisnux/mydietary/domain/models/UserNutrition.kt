package org.cisnux.mydietary.domain.models

data class UserNutrition(
    val totalCaloriesToday: Float = 0f,
    val totalCarbohydrateToday: Float = 0f,
    val totalProteinToday: Float = 0f,
    val totalFatToday: Float = 0f,
    val maxDailyCalories: Float = 0f,
    val maxDailyCarbohydrate: Float = 0f,
    val maxDailyProtein: Float = 0f,
    val maxDailyFat: Float = 0f,
)
