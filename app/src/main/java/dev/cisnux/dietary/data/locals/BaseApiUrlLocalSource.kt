package dev.cisnux.dietary.data.locals

import kotlinx.coroutines.flow.Flow

interface BaseApiUrlLocalSource {
    val baseApiUrl: Flow<String>
    suspend fun updateBaseApiUrl(baseApiUrl: String)
}