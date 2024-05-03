package dev.cisnux.dietary.domain.models

import java.io.File

data class FoodNutrition(
    val totalCalories: Float = 0f,
    val totalProtein: Float = 0f,
    val totalFat: Float = 0f,
    val totalCarbohydrate: Float = 0f,
    val foods: List<Food>,
    val image: Any?
)
