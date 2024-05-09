package org.cisnux.mydietary.data.remotes.bodyrequests

import kotlinx.serialization.Serializable

@Serializable
data class FoodQuestionBodyRequest(
    // the id of food
    val id: String,
    val answers: List<AnswerBodyRequest>
)