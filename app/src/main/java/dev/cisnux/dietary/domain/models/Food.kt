package dev.cisnux.dietary.domain.models

data class Food(
    val id: String,
    val foodName: String,
    val calorie: Float,
    val fat: Float,
    val protein: Float,
    val carbohydrates: Float,
)
