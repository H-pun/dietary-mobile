package org.cisnux.mydietary.data.remotes

import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.cisnux.mydietary.data.dummy.DUMMY_ACCESS_TOKEN
import org.cisnux.mydietary.data.dummy.dummyAddedUserAccount200ResponseJson
import org.cisnux.mydietary.data.dummy.dummyAddedUserAccountIncorrectEmailAddressResponseJson
import org.cisnux.mydietary.data.dummy.dummyAddedUserAccountIncorrectPasswordResponseJson
import org.cisnux.mydietary.data.dummy.dummyUserAccountBodyRequest
import org.cisnux.mydietary.data.dummy.dummyVerifyEmail200ResponseJson
import org.cisnux.mydietary.data.dummy.dummyVerifyEmailBodyRequest
import org.cisnux.mydietary.data.dummy.dummyVerifyEmailInternalServerErrorResponseJson
import org.cisnux.mydietary.data.dummy.expectedAddedUserAccount200Response
import org.cisnux.mydietary.data.dummy.expectedAddedUserAccountIncorrectEmailAddressResponse
import org.cisnux.mydietary.data.dummy.expectedAddedUserAccountIncorrectPasswordResponse
import org.cisnux.mydietary.data.dummy.expectedVerifyEmail200Response
import org.cisnux.mydietary.data.dummy.expectedVerifyEmailInternalServerErrorResponse
import org.cisnux.mydietary.data.locals.BaseApiUrlLocalSource
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class UserAccountRemoteSourceTest : BaseRemoteTest() {
    @MockK
    private lateinit var baseApiUrlLocalSource: BaseApiUrlLocalSource

    @Test
    fun testInit() {
        Assertions.assertNotNull(baseApiUrlLocalSource)
    }

    @Nested
    @DisplayName("when sign in user account")
    inner class SignInUserAccount {

        @Test
        fun `by correct email address and password then should return (200 Success)`() = runTest {
            // arrange
            every { baseApiUrlLocalSource.baseApiUrl } returns flow { emit("") }
            val userAccountRemoteSource = UserAccountRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyAddedUserAccount200ResponseJson,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = userAccountRemoteSource.signInUserAccount(dummyUserAccountBodyRequest)

            // assert
            Assertions.assertEquals(expectedAddedUserAccount200Response, result)
            verify(exactly = 1) { baseApiUrlLocalSource.baseApiUrl }
        }

        @Test
        fun `by incorrect password then should return (401 Unauthorized)`() = runTest {
            // arrange
            every { baseApiUrlLocalSource.baseApiUrl } returns flow { emit("") }
            val userAccountRemoteSource = UserAccountRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyAddedUserAccountIncorrectPasswordResponseJson,
                        status = HttpStatusCode.Unauthorized,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = userAccountRemoteSource.signInUserAccount(dummyUserAccountBodyRequest)

            // assert
            Assertions.assertEquals(
                expectedAddedUserAccountIncorrectPasswordResponse.value.message,
                result.leftOrNull()?.message
            )
            verify(exactly = 1) { baseApiUrlLocalSource.baseApiUrl }
        }

        @Test
        fun `by incorrect email address then should return (401 Unauthorized)`() = runTest {
            // arrange
            every { baseApiUrlLocalSource.baseApiUrl } returns flow { emit("") }
            val userAccountRemoteSource = UserAccountRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyAddedUserAccountIncorrectEmailAddressResponseJson,
                        status = HttpStatusCode.Unauthorized,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = userAccountRemoteSource.signInUserAccount(dummyUserAccountBodyRequest)

            // assert
            Assertions.assertEquals(
                expectedAddedUserAccountIncorrectEmailAddressResponse.value.message,
                result.leftOrNull()?.message
            )
            verify(exactly = 1) { baseApiUrlLocalSource.baseApiUrl }
        }

        @Test
        fun `by correct email address and password but server got error then should return (500 Internal Server Error)`() = runTest {
            // arrange
            every { baseApiUrlLocalSource.baseApiUrl } returns flow { emit("") }
            val userAccountRemoteSource = UserAccountRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyVerifyEmailInternalServerErrorResponseJson,
                        status = HttpStatusCode.InternalServerError,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = userAccountRemoteSource.signInUserAccount(dummyUserAccountBodyRequest)

            // assert
            Assertions.assertEquals(
                expectedVerifyEmailInternalServerErrorResponse.value.message,
                result.leftOrNull()?.message
            )
            verify(exactly = 1) { baseApiUrlLocalSource.baseApiUrl }
        }
    }

    @Nested
    @DisplayName("when verify email address")
    inner class VerifyEmailAddress {

        @Test
        fun `by valid email addresss then should return (200 Success)`() = runTest {
            // arrange
            every { baseApiUrlLocalSource.baseApiUrl } returns flow { emit("") }
            val userAccountRemoteSource = UserAccountRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyVerifyEmail200ResponseJson,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = userAccountRemoteSource.verifyEmailAddress(
                accessToken = DUMMY_ACCESS_TOKEN,
                verifyEmailBodyRequest = dummyVerifyEmailBodyRequest
            )

            // assert
            Assertions.assertEquals(expectedVerifyEmail200Response, result)
            verify(exactly = 1) { baseApiUrlLocalSource.baseApiUrl }
        }

        @Test
        fun `by valid email address but server got error then should return (500 Internal Server Error)`() = runTest {
            // arrange
            every { baseApiUrlLocalSource.baseApiUrl } returns flow { emit("") }
            val userAccountRemoteSource = UserAccountRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyVerifyEmailInternalServerErrorResponseJson,
                        status = HttpStatusCode.InternalServerError,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = userAccountRemoteSource.verifyEmailAddress(accessToken = DUMMY_ACCESS_TOKEN, verifyEmailBodyRequest = dummyVerifyEmailBodyRequest)

            // assert
            Assertions.assertEquals(
                expectedVerifyEmailInternalServerErrorResponse.value.message,
                result.leftOrNull()?.message
            )
            verify(exactly = 1) { baseApiUrlLocalSource.baseApiUrl }
        }
    }


}