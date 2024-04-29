package dev.cisnux.dietary.data.remotes.responses

import kotlinx.serialization.Serializable

@Serializable
data class PredictedFoodDetailResponse(
    val id: String,
    val name: String,
    val serving: List<ServingResponse>,
    val questions: List<QuestionResponse>
)
