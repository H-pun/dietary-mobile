package org.cisnux.mydietary.data.remotes.bodyrequests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReportBodyRequest(
    @SerialName("idUser")
    val userId: String,
    val reportType: String
)
