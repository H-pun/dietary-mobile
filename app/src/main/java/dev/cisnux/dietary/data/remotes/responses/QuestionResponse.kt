package dev.cisnux.dietary.data.remotes.responses

import kotlinx.serialization.Serializable

@Serializable
data class QuestionResponse(
    val id: String,
    // e.g. gula, karbohidrat or minyak
    val label: String,
    val question: String,
    // The type of question can be text, integer, float or boolean
    val type: String,
    val choices: List<String>,
    // e.g. kg or g
    val unit: String? = null,
)