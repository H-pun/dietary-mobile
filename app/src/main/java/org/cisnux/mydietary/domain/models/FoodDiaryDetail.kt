package org.cisnux.mydietary.domain.models

data class FoodDiaryDetail(
    val id: String,
    val title: String,
    val status: String?,
    val feedback: List<String>,
    val foodNutrition: FoodNutrition,
)