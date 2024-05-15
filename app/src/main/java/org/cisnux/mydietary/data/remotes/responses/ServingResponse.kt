package org.cisnux.mydietary.data.remotes.responses

import kotlinx.serialization.Serializable

@Serializable
data class ServingResponse(
    val calories: Float,
    val fat: Float,
    val protein: Float,
    val carbohydrate: Float,
    val sugar: Float?,
    val metricServingAmount: Float
)