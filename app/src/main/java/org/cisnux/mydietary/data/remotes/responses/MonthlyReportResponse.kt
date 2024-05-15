package org.cisnux.mydietary.data.remotes.responses

import kotlinx.serialization.Serializable

@Serializable
data class MonthlyReportResponse(
    val week: Int,
    val startDate: String,
    val endDate: String,
    override val averageCalories: Float,
    override val averageCarbohydrate: Float,
    override val averageProtein: Float,
    override val averageFat: Float,
):ReportResponse