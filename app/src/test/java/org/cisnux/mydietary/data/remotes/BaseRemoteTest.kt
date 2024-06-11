package org.cisnux.mydietary.data.remotes

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockEngineConfig
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.serialization.kotlinx.json.json
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import org.cisnux.mydietary.data.locals.BaseApiUrlLocalSource
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
open class BaseRemoteTest {
    private val mockConfig = MockEngineConfig()

    @MockK
    protected lateinit var baseApiUrlLocalSource: BaseApiUrlLocalSource


    @BeforeEach
    fun setUp() {
        every { baseApiUrlLocalSource.baseApiUrl } returns flow { emit("") }
    }

    @AfterEach
    fun tearDown() {
        verify(exactly = 1) { baseApiUrlLocalSource.baseApiUrl }
    }

    fun mockHandler(handler: MockRequestHandleScope.(HttpRequestData) -> HttpResponseData): HttpClient {
        mockConfig.addHandler(handler)
        val mockEngine = MockEngine(config = mockConfig)
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
        }
        return client
    }
}