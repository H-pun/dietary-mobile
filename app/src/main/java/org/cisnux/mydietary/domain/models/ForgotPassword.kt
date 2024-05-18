package org.cisnux.mydietary.domain.models

data class ForgotPassword(
    val code: String,
    val emailAddress: String,
    val newPassword: String
)
