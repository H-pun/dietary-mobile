package dev.cisnux.dietary.presentation.scannerresult

import android.os.Parcelable
import dev.cisnux.dietary.utils.QuestionType
import kotlinx.parcelize.Parcelize

@Parcelize
data class AnsweredQuestion(
    val questionId: String,
    val label: String,
    val question: String,
    val type: QuestionType,
    val unit: String?,
    var answer: String,
) : Parcelable
