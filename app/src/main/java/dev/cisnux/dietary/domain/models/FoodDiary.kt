package dev.cisnux.dietary.domain.models

data class FoodDiary(
    val id: String,
    val foodName: String,
    val date: String,
    val time: String,
    val foodImageUrl: String,
    val calorie: Float,
)
