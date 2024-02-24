package dev.cisnux.dietary.domain.models

data class FoodDiary(
    val id: String,
    val title: String,
    val date: String,
    val time: String,
    val foodPictureUrl: String?,
    val totalFoodCalories: Float,
)
