package org.cisnux.mydietary.data.remotes.responses

import kotlinx.serialization.Serializable

@Serializable
data class FoodInformationResponse(
    val id: Int,
    val name: String
)
