package org.cisnux.mydietary.data.remotes.responses

import kotlinx.serialization.Serializable

@Serializable
data class ReportResponse(
    val week: Int,
    val startDate: String,
    val endDate: String,
    val averageCalories: Float,
    val averageCarbohydrate: Float,
    val averageProtein: Float,
    val averageFat: Float,
)