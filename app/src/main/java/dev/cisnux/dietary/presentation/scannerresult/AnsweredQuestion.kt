package dev.cisnux.dietary.presentation.scannerresult

import android.os.Parcelable
import dev.cisnux.dietary.utils.QuestionType
import kotlinx.parcelize.Parcelize

@Parcelize
data class AnsweredQuestion(
    val questionId: String,
    val question: String,
    var answer: String,
    val choices: List<String>
) : Parcelable
