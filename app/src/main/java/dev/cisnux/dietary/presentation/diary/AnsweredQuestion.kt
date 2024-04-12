package dev.cisnux.dietary.presentation.diary

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AnsweredQuestion(
    val questionId: String,
    val question: String,
    var answer: String,
    val choices: List<String>
) : Parcelable
