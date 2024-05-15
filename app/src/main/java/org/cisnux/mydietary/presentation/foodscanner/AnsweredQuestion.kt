package org.cisnux.mydietary.presentation.foodscanner

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AnsweredQuestion(
    val questionId: String,
    val question: String,
    var choice: Choice? = null,
    val choices: List<Choice>
) : Parcelable {
    @Parcelize
    data class Choice(val id: String, val answer: String, val reference: String?) : Parcelable
}
