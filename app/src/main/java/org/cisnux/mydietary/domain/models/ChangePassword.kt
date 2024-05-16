package org.cisnux.mydietary.domain.models

data class ChangePassword(
    val code: String,
    val emailAddress: String,
    val newPassword: String
)
