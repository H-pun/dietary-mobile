package org.cisnux.mydietary.domain.models

data class Report(
    val maxCalories: Float,
    val maxProtein: Float,
    val maxFat: Float,
    val maxCarbohydrate: Float,
    val reports: List<FoodDiaryReport>
)
