package org.cisnux.mydietary.data.remotes.responses

import kotlinx.serialization.Serializable

// The food which was detected
@Serializable
data class FoodResponse(
    val id: String,
    val name: String,
    val calorie: Float,
    val fat: Float,
    val protein: Float,
    val carbohydrates: Float,
    // it can be null
    val sugar: Float? = null,
)