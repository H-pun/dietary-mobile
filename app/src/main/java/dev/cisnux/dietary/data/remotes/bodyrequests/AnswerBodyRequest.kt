package dev.cisnux.dietary.data.remotes.bodyrequests

import kotlinx.serialization.Serializable

// The answer from user
@Serializable
data class AnswerBodyRequest(
    // the id of question
    val id: String,
    val question: String,
    val answer: String,
    // The type of question can be text, integer, float or boolean
    val type: String
)