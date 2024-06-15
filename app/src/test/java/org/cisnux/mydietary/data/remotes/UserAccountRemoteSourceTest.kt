package org.cisnux.mydietary.data.remotes

import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.cisnux.mydietary.commons.utils.DUMMY_ACCESS_TOKEN
import org.cisnux.mydietary.commons.utils.dummyAddedUserAccount200ResponseJson
import org.cisnux.mydietary.commons.utils.dummyAddedUserAccountIncorrectPasswordResponseJson
import org.cisnux.mydietary.commons.utils.dummyUserAccountBodyRequest
import org.cisnux.mydietary.commons.utils.dummyVerifyEmail200ResponseJson
import org.cisnux.mydietary.commons.utils.dummyVerifyEmailBodyRequest
import org.cisnux.mydietary.commons.utils.expectedAddedUserAccount200Response
import org.cisnux.mydietary.commons.utils.expectedAddedUserAccountIncorrectPasswordResponse
import org.cisnux.mydietary.commons.utils.expectedVerifyEmail200Response
import org.cisnux.mydietary.commons.utils.expectedInternalServerErrorResponse
import org.cisnux.mydietary.commons.utils.dummyAddedUserAccount201ResponseJson
import org.cisnux.mydietary.commons.utils.dummyChangePassword200ResponseJson
import org.cisnux.mydietary.commons.utils.dummyChangePassword401ResponseJson
import org.cisnux.mydietary.commons.utils.dummyChangePasswordBodyRequest
import org.cisnux.mydietary.commons.utils.dummyGoogleTokenBodyRequest
import org.cisnux.mydietary.commons.utils.dummyInternalServerErrorResponseJson
import org.cisnux.mydietary.commons.utils.dummyUpdateEmail200ResponseJson
import org.cisnux.mydietary.commons.utils.dummyEmailAddress400ResponseJson
import org.cisnux.mydietary.commons.utils.dummyForgotPassword200ResponseJson
import org.cisnux.mydietary.commons.utils.dummyForgotPassword401ResponseJson
import org.cisnux.mydietary.commons.utils.dummyNewPasswordBodyRequest
import org.cisnux.mydietary.commons.utils.dummySendResetPassword200ResponseJson
import org.cisnux.mydietary.commons.utils.dummySendResetPassword401ResponseJson
import org.cisnux.mydietary.commons.utils.dummySendResetPasswordBodyRequest
import org.cisnux.mydietary.commons.utils.dummyUpdateEmailBodyRequest
import org.cisnux.mydietary.commons.utils.expectedChangePassword200Response
import org.cisnux.mydietary.commons.utils.expectedChangePassword401Response
import org.cisnux.mydietary.commons.utils.expectedUpdateEmail200Response
import org.cisnux.mydietary.commons.utils.expectedEmail400Response
import org.cisnux.mydietary.commons.utils.expectedForgotPassword200Response
import org.cisnux.mydietary.commons.utils.expectedForgotPassword401Response
import org.cisnux.mydietary.commons.utils.expectedSendResetPassword200Response
import org.cisnux.mydietary.commons.utils.expectedSendResetPassword401Response
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
@DisplayName("User Account")
class UserAccountRemoteSourceTest : BaseRemoteTest() {

    @Nested
    @DisplayName("when sign in with email address and password")
    inner class SignInWithEmailAddressAndPassword {

        @Test
        fun `by correct email address and password then should return (200 OK)`() = runTest {
            // arrange
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
            val result = userAccountRemoteSource.signInWithEmailAddressAndPassword(
                dummyUserAccountBodyRequest
            )

            // assert
            Assertions.assertEquals(expectedAddedUserAccount200Response, result)
        }

        @Test
        fun `by incorrect password then should return (401 Unauthorized)`() = runTest {
            // arrange
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
            val result = userAccountRemoteSource.signInWithEmailAddressAndPassword(
                dummyUserAccountBodyRequest
            )

            // assert
            Assertions.assertEquals(
                expectedAddedUserAccountIncorrectPasswordResponse.value.message,
                result.leftOrNull()?.message
            )
        }

        @Test
        fun `by correct email address and password but server got internal error then should return (500 Internal Server Error)`() =
            runTest {
                // arrange
                val userAccountRemoteSource = UserAccountRemoteSourceImpl(
                    baseApiUrlLocalSource = baseApiUrlLocalSource,
                    client = mockHandler {
                        respond(
                            content = dummyInternalServerErrorResponseJson,
                            status = HttpStatusCode.InternalServerError,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    })

                // act
                val result = userAccountRemoteSource.signInWithEmailAddressAndPassword(
                    dummyUserAccountBodyRequest
                )

                // assert
                Assertions.assertEquals(
                    expectedInternalServerErrorResponse.value.message,
                    result.leftOrNull()?.message
                )
            }
    }

    @Nested
    @DisplayName("when verify email address")
    inner class VerifyEmailAddress {

        @Test
        fun `by valid email address then should return (200 OK)`() = runTest {
            // arrange
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
        }

        @Test
        fun `by valid email address but server got internal error then should return (500 Internal Server Error)`() =
            runTest {
                // arrange
                val userAccountRemoteSource = UserAccountRemoteSourceImpl(
                    baseApiUrlLocalSource = baseApiUrlLocalSource,
                    client = mockHandler {
                        respond(
                            content = dummyInternalServerErrorResponseJson,
                            status = HttpStatusCode.InternalServerError,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    })

                // act
                val result = userAccountRemoteSource.verifyEmailAddress(
                    accessToken = DUMMY_ACCESS_TOKEN,
                    verifyEmailBodyRequest = dummyVerifyEmailBodyRequest
                )

                // assert
                Assertions.assertEquals(
                    expectedInternalServerErrorResponse.value.message,
                    result.leftOrNull()?.message
                )
            }
    }

    @Nested
    @DisplayName("when update email address")
    inner class UpdateEmailAddress {

        @Test
        fun `by valid email address then should return (200 OK)`() = runTest {
            // arrange
            val userAccountRemoteSource = UserAccountRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyUpdateEmail200ResponseJson,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = userAccountRemoteSource.updateEmail(
                accessToken = DUMMY_ACCESS_TOKEN,
                updateEmailBodyRequest = dummyUpdateEmailBodyRequest
            )

            // assert
            Assertions.assertEquals(expectedUpdateEmail200Response, result)
        }

        @Test
        fun `by email address that already used then should return (400 Bad Request)`() = runTest {
            // arrange
            val userAccountRemoteSource = UserAccountRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyEmailAddress400ResponseJson,
                        status = HttpStatusCode.BadRequest,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = userAccountRemoteSource.updateEmail(
                updateEmailBodyRequest = dummyUpdateEmailBodyRequest,
                accessToken = DUMMY_ACCESS_TOKEN
            )

            // assert
            Assertions.assertEquals(
                expectedEmail400Response.value.message,
                result.leftOrNull()?.message
            )
        }

        @Test
        fun `by valid email address but server got internal error then should return (500 Internal Server Error)`() =
            runTest {
                // arrange
                val userAccountRemoteSource = UserAccountRemoteSourceImpl(
                    baseApiUrlLocalSource = baseApiUrlLocalSource,
                    client = mockHandler {
                        respond(
                            content = dummyInternalServerErrorResponseJson,
                            status = HttpStatusCode.InternalServerError,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    })

                // act
                val result = userAccountRemoteSource.updateEmail(
                    updateEmailBodyRequest = dummyUpdateEmailBodyRequest,
                    accessToken = DUMMY_ACCESS_TOKEN
                )

                // assert
                Assertions.assertEquals(
                    expectedInternalServerErrorResponse.value.message,
                    result.leftOrNull()?.message
                )
            }
    }

    @Nested
    @DisplayName("when sign in with Google")
    inner class SignInWithGoogle {

        @Test
        fun `by valid Google email address then should return (200 OK)`() = runTest {
            // arrange
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
            val result = userAccountRemoteSource.signInWithGoogle(
                googleToken = dummyGoogleTokenBodyRequest,
            )

            // assert
            Assertions.assertEquals(expectedAddedUserAccount200Response, result)
        }

        @Test
        fun `by valid Google email address but server got internal error then should return (500 Internal Server Error)`() =
            runTest {
                // arrange
                val userAccountRemoteSource = UserAccountRemoteSourceImpl(
                    baseApiUrlLocalSource = baseApiUrlLocalSource,
                    client = mockHandler {
                        respond(
                            content = dummyInternalServerErrorResponseJson,
                            status = HttpStatusCode.InternalServerError,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    })

                // act
                val result = userAccountRemoteSource.signInWithGoogle(
                    googleToken = dummyGoogleTokenBodyRequest,
                )

                // assert
                Assertions.assertEquals(
                    expectedInternalServerErrorResponse.value.message,
                    result.leftOrNull()?.message
                )
            }
    }

    @Nested
    @DisplayName("when sign up with email address and password")
    inner class SignUp {

        @Test
        fun `by valid email address and password then should return (201 Created)`() = runTest {
            // arrange
            val userAccountRemoteSource = UserAccountRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyAddedUserAccount201ResponseJson,
                        status = HttpStatusCode.Created,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = userAccountRemoteSource.signUp(
                userAccount = dummyUserAccountBodyRequest
            )

            // assert
            Assertions.assertTrue(result.isRight())
        }

        @Test
        fun `by email address that already used then should return (400 Bad Request)`() = runTest {
            // arrange
            val userAccountRemoteSource = UserAccountRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyEmailAddress400ResponseJson,
                        status = HttpStatusCode.BadRequest,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = userAccountRemoteSource.signUp(
                userAccount = dummyUserAccountBodyRequest
            )

            // assert
            Assertions.assertEquals(
                expectedEmail400Response.value.message,
                result.leftOrNull()?.message
            )
        }

        @Test
        fun `by valid email address and password but server got internal error then should return (500 Internal Server Error)`() =
            runTest {
                // arrange
                val userAccountRemoteSource = UserAccountRemoteSourceImpl(
                    baseApiUrlLocalSource = baseApiUrlLocalSource,
                    client = mockHandler {
                        respond(
                            content = dummyInternalServerErrorResponseJson,
                            status = HttpStatusCode.InternalServerError,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    })

                // act
                val result = userAccountRemoteSource.signUp(
                    userAccount = dummyUserAccountBodyRequest
                )

                // assert
                Assertions.assertEquals(
                    expectedInternalServerErrorResponse.value.message,
                    result.leftOrNull()?.message
                )
            }
    }

    @Nested
    @DisplayName("when change password")
    inner class ChangePassword {

        @Test
        fun `by correct old password then should return (200 OK)`() = runTest {
            // arrange
            val userAccountRemoteSource = UserAccountRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyChangePassword200ResponseJson,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = userAccountRemoteSource.changePassword(
                changePasswordBodyRequest = dummyChangePasswordBodyRequest,
                accessToken = DUMMY_ACCESS_TOKEN
            )

            // assert
            Assertions.assertEquals(expectedChangePassword200Response, result)
        }

        @Test
        fun `by incorrect old password then should return (401 Unauthorized)`() = runTest {
            // arrange
            val userAccountRemoteSource = UserAccountRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyChangePassword401ResponseJson,
                        status = HttpStatusCode.Unauthorized,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = userAccountRemoteSource.changePassword(
                accessToken = DUMMY_ACCESS_TOKEN,
                changePasswordBodyRequest = dummyChangePasswordBodyRequest
            )

            // assert
            Assertions.assertEquals(
                expectedChangePassword401Response.value.message,
                result.leftOrNull()?.message
            )
        }

        @Test
        fun `by correct old password but server got internal error then should return (500 Internal Server Error)`() =
            runTest {
                // arrange
                val userAccountRemoteSource = UserAccountRemoteSourceImpl(
                    baseApiUrlLocalSource = baseApiUrlLocalSource,
                    client = mockHandler {
                        respond(
                            content = dummyInternalServerErrorResponseJson,
                            status = HttpStatusCode.InternalServerError,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    })

                // act
                val result = userAccountRemoteSource.changePassword(
                    accessToken = DUMMY_ACCESS_TOKEN,
                    changePasswordBodyRequest = dummyChangePasswordBodyRequest
                )

                // assert
                Assertions.assertEquals(
                    expectedInternalServerErrorResponse.value.message,
                    result.leftOrNull()?.message
                )
            }
    }

    @Nested
    @DisplayName("when send reset password")
    inner class SendResetPassword {

        @Test
        fun `by verified email address then should return (200 OK)`() = runTest {
            // arrange
            val userAccountRemoteSource = UserAccountRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummySendResetPassword200ResponseJson,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = userAccountRemoteSource.sendResetPassword(
                resetPassword = dummySendResetPasswordBodyRequest,
            )

            // assert
            Assertions.assertEquals(expectedSendResetPassword200Response, result)
        }

        @Test
        fun `by unverified email address then should return (401 Unauthorized)`() = runTest {
            // arrange
            val userAccountRemoteSource = UserAccountRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummySendResetPassword401ResponseJson,
                        status = HttpStatusCode.Unauthorized,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = userAccountRemoteSource.sendResetPassword(
                resetPassword = dummySendResetPasswordBodyRequest
            )

            // assert
            Assertions.assertEquals(
                expectedSendResetPassword401Response.value.message,
                result.leftOrNull()?.message
            )
        }

        @Test
        fun `by verified email address but server got internal error then should return (500 Internal Server Error)`() =
            runTest {
                // arrange
                val userAccountRemoteSource = UserAccountRemoteSourceImpl(
                    baseApiUrlLocalSource = baseApiUrlLocalSource,
                    client = mockHandler {
                        respond(
                            content = dummyInternalServerErrorResponseJson,
                            status = HttpStatusCode.InternalServerError,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    })

                // act
                val result = userAccountRemoteSource.sendResetPassword(
                    resetPassword = dummySendResetPasswordBodyRequest
                )

                // assert
                Assertions.assertEquals(
                    expectedInternalServerErrorResponse.value.message,
                    result.leftOrNull()?.message
                )
            }
    }

    @Nested
    @DisplayName("when forgot password")
    inner class ForgotPassword {

        @Test
        fun `by valid OTP then should return (200 OK)`() = runTest {
            // arrange
            val userAccountRemoteSource = UserAccountRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyForgotPassword200ResponseJson,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = userAccountRemoteSource.forgotPassword(
                newPassword = dummyNewPasswordBodyRequest
            )
            // assert
            Assertions.assertEquals(expectedForgotPassword200Response, result)
        }

        @Test
        fun `by expired OTP then should return (401 Unauthorized)`() = runTest {
            // arrange
            val userAccountRemoteSource = UserAccountRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyForgotPassword401ResponseJson,
                        status = HttpStatusCode.Unauthorized,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = userAccountRemoteSource.forgotPassword(
                newPassword = dummyNewPasswordBodyRequest
            )

            // assert
            Assertions.assertEquals(
                expectedForgotPassword401Response.value.message,
                result.leftOrNull()?.message
            )
        }

        @Test
        fun `by valid OTP but server got internal error then should return (500 Internal Server Error)`() =
            runTest {
                // arrange
                val userAccountRemoteSource = UserAccountRemoteSourceImpl(
                    baseApiUrlLocalSource = baseApiUrlLocalSource,
                    client = mockHandler {
                        respond(
                            content = dummyInternalServerErrorResponseJson,
                            status = HttpStatusCode.InternalServerError,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    })

                // act
                val result = userAccountRemoteSource.forgotPassword(
                    newPassword = dummyNewPasswordBodyRequest
                )

                // assert
                Assertions.assertEquals(
                    expectedInternalServerErrorResponse.value.message,
                    result.leftOrNull()?.message
                )
            }
    }
}