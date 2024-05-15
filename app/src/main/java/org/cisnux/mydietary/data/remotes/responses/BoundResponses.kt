package org.cisnux.mydietary.data.remotes.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BoundResponses(
    @SerialName("bounds")
    val bound: BoundResponse
)
