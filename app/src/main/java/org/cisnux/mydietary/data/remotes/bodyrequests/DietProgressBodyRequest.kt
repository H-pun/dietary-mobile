package org.cisnux.mydietary.data.remotes.bodyrequests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.concurrent.Flow

@Serializable
data class DietProgressBodyRequest(
    @SerialName("idUser")
    val id: String,
    val weight: Float,
    val waistCircumference: Float,
    val updatedAt: String
)
