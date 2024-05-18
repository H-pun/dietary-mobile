package org.cisnux.mydietary.domain.models

data class ChangePassword(
    val oldPassword: String,
    val newPassword: String
)
