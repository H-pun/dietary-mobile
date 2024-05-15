package org.cisnux.mydietary.domain.models

data class FoodNutrition(
    val totalCalories: Float = 0f,
    val totalProtein: Float = 0f,
    val totalFat: Float = 0f,
    val totalCarbohydrate: Float = 0f,
    val foods: List<Food>,
    val image: Any?
)
