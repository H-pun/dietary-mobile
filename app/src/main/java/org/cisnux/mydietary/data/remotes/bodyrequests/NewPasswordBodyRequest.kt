package org.cisnux.mydietary.data.remotes.bodyrequests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// The body request to add a new password
@Serializable
data class NewPasswordBodyRequest(
    // the id of user account
    val id: String,
    @SerialName(value = "new_password")
    val newPassword: String
)
