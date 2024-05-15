package org.cisnux.mydietary.data.remotes.responses

import kotlinx.serialization.Serializable

@Serializable
sealed interface ReportResponse {
    val averageCalories: Float
    val averageCarbohydrate: Float
    val averageProtein: Float
    val averageFat: Float
}