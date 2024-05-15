package org.cisnux.mydietary.domain.models

data class Report(
    val datePages: List<DatePage> = listOf(),
    val chunkedFoodDiaries: List<List<FoodDiaryReport>> = listOf()
) {
    data class DatePage(
        val dateRange: String = ""
    )
}
