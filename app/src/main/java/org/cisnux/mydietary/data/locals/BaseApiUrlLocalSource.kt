package org.cisnux.mydietary.data.locals

import kotlinx.coroutines.flow.Flow

interface BaseApiUrlLocalSource {
    val baseApiUrl: Flow<String>
    suspend fun updateBaseApiUrl(baseApiUrl: String)
}