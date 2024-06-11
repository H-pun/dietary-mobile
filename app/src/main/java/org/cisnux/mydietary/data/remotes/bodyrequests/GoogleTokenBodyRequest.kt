package org.cisnux.mydietary.data.remotes.bodyrequests

import kotlinx.serialization.Serializable

@Serializable
data class GoogleTokenBodyRequest(
    val idToken: String
)
