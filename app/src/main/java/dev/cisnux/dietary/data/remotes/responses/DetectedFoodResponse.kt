package dev.cisnux.dietary.data.remotes.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DetectedFoodResponse(
    val foodDetail: PredictedFoodDetailResponse,
    @SerialName("predictResult")
    val bounds: BoundResponses
)
