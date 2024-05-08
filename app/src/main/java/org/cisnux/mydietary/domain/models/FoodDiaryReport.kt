package org.cisnux.mydietary.domain.models

data class FoodDiaryReport(
    val averageCalories: Float,
    val averageProtein: Float,
    val averageFat: Float,
    val averageCarbohydrate: Float,
    val label: String,
    val description: String
)
