package dev.cisnux.dietary.data.remotes.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddedUserAccountResponse(
    val id: String,
    @SerialName("email")
    val emailAddress: String,
    @SerialName("appToken")
    val accessToken: String
)
