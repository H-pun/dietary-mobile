package org.cisnux.mydietary.data.remotes.responses

import kotlinx.serialization.Serializable

@Serializable
data class WeeklyReportResponse(
    val date: String,
    val averageCalories: Float,
    val averageCarbohydrate: Float,
    val averageProtein: Float,
    val averageFat: Float,
)
