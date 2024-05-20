package org.cisnux.mydietary.domain.models

data class WeeklyNutritionReport(
    val weeks: List<DatePage> = listOf(),
    val weekFoodDiaryReports: List<List<FoodDiaryReport>> = listOf()
) {
    data class DatePage(
        val dateRange: String = ""
    )
}
