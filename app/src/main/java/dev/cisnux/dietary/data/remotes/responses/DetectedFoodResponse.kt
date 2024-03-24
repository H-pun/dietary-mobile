package dev.cisnux.dietary.data.remotes.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DetectedFoodResponse(
    val id: Int,
    val name: String,
    val calorie: Float,
    val fat: Float,
    val protein: Float,
    val carbohydrates: Float,
    // it can be null
    val sugar: Float? = null,
    @SerialName("bounds")
    val bound: BoundResponse,
    val questions: List<QuestionResponse>? = null
)
