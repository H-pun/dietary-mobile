package org.cisnux.mydietary.data.remotes.bodyrequests

import kotlinx.serialization.Serializable

// The body request to update food diary
// by added user's answers
@Serializable
data class DiaryQuestionBodyRequest(
    // the id of food diary
    val id: String,
    val answers: List<FoodQuestionBodyRequest>
)