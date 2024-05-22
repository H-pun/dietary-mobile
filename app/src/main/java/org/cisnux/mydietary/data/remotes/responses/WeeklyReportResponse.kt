package org.cisnux.mydietary.data.remotes.responses

import kotlinx.serialization.Serializable

@Serializable
data class WeeklyReportResponse(
    val date: String,
    override val averageCalories: Float,
    override val averageCarbohydrate: Float,
    override val averageProtein: Float,
    override val averageFat: Float,
) : ReportResponse
