package dev.cisnux.dietary.data.remotes.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PredictedFoodDetailResponse(
    val id: String,
    val name: String,
    val questions: List<QuestionResponse>
)
