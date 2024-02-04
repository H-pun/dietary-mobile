package dev.cisnux.dietary.data.remotes.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReportResponse(
    @SerialName(value = "total_user_calories_today")
    val totalUserCaloriesToday: Float,
    @SerialName(value = "max_daily_bmr_calories")
    val maxDailyBmrCalorie: Float,
    val foods: List<FoodDiaryReportResponse>
)