package dev.cisnux.dietary.domain.models

data class DiaryFood(
    val id: String,
    val foodName: String,
    val date: String,
    val time: String,
    val foodImageUrl: String,
    val calorie: Float,
)
