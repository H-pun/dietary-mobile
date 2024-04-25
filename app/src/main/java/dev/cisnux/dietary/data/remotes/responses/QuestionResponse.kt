package dev.cisnux.dietary.data.remotes.responses

import kotlinx.serialization.Serializable

@Serializable
data class QuestionResponse(
    val id: String,
    val question: String,
    val options: List<OptionResponse>,
)