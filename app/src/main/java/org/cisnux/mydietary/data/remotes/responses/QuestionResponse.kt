package org.cisnux.mydietary.data.remotes.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuestionResponse(
    val id: String,
    @SerialName("context")
    val question: String,
    val type: String,
    val options: List<OptionResponse>,
)