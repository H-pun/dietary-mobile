package org.cisnux.mydietary.domain.models

data class FoodDiaryReport(
    val averageCalories: Float,
    val averageProtein: Float,
    val averageFat: Float,
    val averageCarbohydrate: Float,
    val label: String,
    val description: String,
) {
    var date: String? = null
        private set

    constructor(
        averageCalories: Float,
        averageProtein: Float,
        averageFat: Float,
        averageCarbohydrate: Float,
        label: String,
        description: String, date: String?
    ) : this(averageCalories, averageProtein, averageFat, averageCarbohydrate, label, description) {
        this.date = date
    }
}
