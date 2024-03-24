package dev.cisnux.dietary.data.remotes.responses

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class PredictedResponse(
    @SerialName("boxes")
    val foods: List<PredictedFoodResponse>
)
