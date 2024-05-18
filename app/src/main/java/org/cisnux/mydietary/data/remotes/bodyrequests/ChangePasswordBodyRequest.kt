package org.cisnux.mydietary.data.remotes.bodyrequests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordBodyRequest(
    @SerialName("idUser")
    val id: String,
    val oldPassword: String,
    val newPassword: String
)
