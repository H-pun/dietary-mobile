package dev.cisnux.dietary.domain.models

import java.io.File

data class AddFoodDiary(
    val title: String,
    val category: String,
    val foodPicture: File,
    val feedback: List<String>,
    val foodIds: List<String>,
    val totalCalories: Float,
    val totalProtein: Float,
    val totalFat: Float,
    val totalCarbohydrate: Float,
)
