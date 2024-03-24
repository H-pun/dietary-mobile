package dev.cisnux.dietary.data.remotes.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PredictedFoodResponse(
    @SerialName("class")
    val foodInformationResponse: FoodInformationResponse,
    @SerialName("bounds")
    val bound: BoundResponse
)
