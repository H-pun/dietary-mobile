package org.cisnux.mydietary.data.remotes

import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.cisnux.mydietary.commons.utils.DUMMY_ACCESS_TOKEN
import org.cisnux.mydietary.commons.utils.DUMMY_DATE
import org.cisnux.mydietary.commons.utils.DUMMY_USER_ACCOUNT_ID
import org.cisnux.mydietary.commons.utils.Failure
import org.cisnux.mydietary.commons.utils.dummyAddUserProfile201ResponseJson
import org.cisnux.mydietary.commons.utils.dummyDietProgressBodyRequest
import org.cisnux.mydietary.commons.utils.dummyExpiredAccessTokenResponseJson
import org.cisnux.mydietary.commons.utils.dummyGetDietProgress200ResponseJson
import org.cisnux.mydietary.commons.utils.dummyInternalServerErrorResponseJson
import org.cisnux.mydietary.commons.utils.dummyNewUserProfileBodyRequest
import org.cisnux.mydietary.commons.utils.dummyNutrient200ResponseJson
import org.cisnux.mydietary.commons.utils.dummyUpdateDietProgress200ResponseJson
import org.cisnux.mydietary.commons.utils.dummyUpdateUserProfile200ResponseJson
import org.cisnux.mydietary.commons.utils.dummyUpdateUserProfileBodyRequest
import org.cisnux.mydietary.commons.utils.dummyUserProfile400ResponseJson
import org.cisnux.mydietary.commons.utils.dummyUserAccount404ResponseJson
import org.cisnux.mydietary.commons.utils.dummyUserProfileDetail200ResponseJson
import org.cisnux.mydietary.commons.utils.expectedAddUserProfile201Response
import org.cisnux.mydietary.commons.utils.expectedGetDietProgress200Response
import org.cisnux.mydietary.commons.utils.expectedInternalServerErrorResponse
import org.cisnux.mydietary.commons.utils.expectedNutrient200Response
import org.cisnux.mydietary.commons.utils.expectedUserProfile400Response
import org.cisnux.mydietary.commons.utils.expectedUserAccount404Response
import org.cisnux.mydietary.commons.utils.expectedUserProfileDetail200Response
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
@DisplayName("User Profile")
class UserProfileRemoteSourceTest : BaseRemoteTest() {
    @Nested
    @DisplayName("when add new user profile")
    inner class AddUserProfile {

        @Test
        fun `by valid body request then should return (201 Created)`() = runTest {
            // arrange
            val userProfileRemoteSource = UserProfileRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyAddUserProfile201ResponseJson,
                        status = HttpStatusCode.Created,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = userProfileRemoteSource.addUserProfile(
                accessToken = DUMMY_ACCESS_TOKEN,
                userProfile = dummyNewUserProfileBodyRequest
            )

            // assert
            Assertions.assertEquals(expectedAddUserProfile201Response, result)
        }

        @Test
        fun `by username that already used then should return (400 Bad Request)`() = runTest {
            // arrange
            val userProfileRemoteSource = UserProfileRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyUserProfile400ResponseJson,
                        status = HttpStatusCode.BadRequest,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = userProfileRemoteSource.addUserProfile(
                accessToken = DUMMY_ACCESS_TOKEN,
                userProfile = dummyNewUserProfileBodyRequest
            )

            // assert
            Assertions.assertEquals(
                expectedUserProfile400Response.value.message,
                result.leftOrNull()?.message
            )
        }

        @Test
        fun `by valid body request but access token has expired then should return (401 Unauthorized)`() =
            runTest {
                // arrange
                val userProfileRemoteSource = UserProfileRemoteSourceImpl(
                    baseApiUrlLocalSource = baseApiUrlLocalSource,
                    client = mockHandler {
                        respond(
                            content = dummyExpiredAccessTokenResponseJson,
                            status = HttpStatusCode.Unauthorized,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    })

                // act
                val result = userProfileRemoteSource.addUserProfile(
                    accessToken = DUMMY_ACCESS_TOKEN,
                    userProfile = dummyNewUserProfileBodyRequest
                )

                // assert
                Assertions.assertTrue(result.leftOrNull() is Failure.UnauthorizedFailure)
            }

        @Test
        fun `by valid body request but server got internal error then should return (500 Internal Server Error)`() =
            runTest {
                // arrange
                val userProfileRemoteSource = UserProfileRemoteSourceImpl(
                    baseApiUrlLocalSource = baseApiUrlLocalSource,
                    client = mockHandler {
                        respond(
                            content = dummyInternalServerErrorResponseJson,
                            status = HttpStatusCode.InternalServerError,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    })

                // act
                val result = userProfileRemoteSource.addUserProfile(
                    accessToken = DUMMY_ACCESS_TOKEN,
                    userProfile = dummyNewUserProfileBodyRequest
                )

                // assert
                Assertions.assertEquals(
                    expectedInternalServerErrorResponse.value.message,
                    result.leftOrNull()?.message
                )
            }
    }

    @Nested
    @DisplayName("when update user profile")
    inner class UpdateUserProfile {

        @Test
        fun `by valid body request then should return (200 OK)`() = runTest {
            // arrange
            val userProfileRemoteSource = UserProfileRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyUpdateUserProfile200ResponseJson,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = userProfileRemoteSource.updateUserProfile(
                accessToken = DUMMY_ACCESS_TOKEN,
                userProfile = dummyUpdateUserProfileBodyRequest
            )

            // assert
            Assertions.assertTrue(result.isRight())
        }

        @Test
        fun `by username that already used then should return (400 Bad Request)`() = runTest {
            // arrange
            val userProfileRemoteSource = UserProfileRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyUserProfile400ResponseJson,
                        status = HttpStatusCode.BadRequest,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = userProfileRemoteSource.addUserProfile(
                accessToken = DUMMY_ACCESS_TOKEN,
                userProfile = dummyNewUserProfileBodyRequest
            )

            // assert
            Assertions.assertEquals(
                expectedUserProfile400Response.value.message,
                result.leftOrNull()?.message
            )
        }

        @Test
        fun `by valid body request but access token has expired then should return (401 Unauthorized)`() =
            runTest {
                // arrange
                val userProfileRemoteSource = UserProfileRemoteSourceImpl(
                    baseApiUrlLocalSource = baseApiUrlLocalSource,
                    client = mockHandler {
                        respond(
                            content = dummyExpiredAccessTokenResponseJson,
                            status = HttpStatusCode.Unauthorized,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    })

                // act
                val result = userProfileRemoteSource.updateUserProfile(
                    accessToken = DUMMY_ACCESS_TOKEN,
                    userProfile = dummyUpdateUserProfileBodyRequest
                )

                // assert
                Assertions.assertTrue(result.leftOrNull() is Failure.UnauthorizedFailure)
            }

        @Test
        fun `by valid body request but server got internal error then should return (500 Internal Server Error)`() =
            runTest {
                // arrange
                val userProfileRemoteSource = UserProfileRemoteSourceImpl(
                    baseApiUrlLocalSource = baseApiUrlLocalSource,
                    client = mockHandler {
                        respond(
                            content = dummyInternalServerErrorResponseJson,
                            status = HttpStatusCode.InternalServerError,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    })

                // act
                val result = userProfileRemoteSource.updateUserProfile(
                    accessToken = DUMMY_ACCESS_TOKEN,
                    userProfile = dummyUpdateUserProfileBodyRequest
                )

                // assert
                Assertions.assertEquals(
                    expectedInternalServerErrorResponse.value.message,
                    result.leftOrNull()?.message
                )
                verify(exactly = 1) { baseApiUrlLocalSource.baseApiUrl }
            }
    }

    @Nested
    @DisplayName("when get user profile")
    inner class GetUserProfile {

        @Test
        fun `by valid id then should return (200 OK)`() = runTest {
            // arrange
            val userProfileRemoteSource = UserProfileRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyUserProfileDetail200ResponseJson,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = userProfileRemoteSource.getUserProfile(
                userAccountId = DUMMY_USER_ACCOUNT_ID,
                accessToken = DUMMY_ACCESS_TOKEN
            )

            // assert
            Assertions.assertEquals(expectedUserProfileDetail200Response, result)
        }

        @Test
        fun `by valid id but access token has expired then should return (401 Unauthorized)`() =
            runTest {
                // arrange
                val userProfileRemoteSource = UserProfileRemoteSourceImpl(
                    baseApiUrlLocalSource = baseApiUrlLocalSource,
                    client = mockHandler {
                        respond(
                            content = dummyExpiredAccessTokenResponseJson,
                            status = HttpStatusCode.Unauthorized,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    })

                // act
                val result = userProfileRemoteSource.getUserProfile(
                    userAccountId = DUMMY_USER_ACCOUNT_ID,
                    accessToken = DUMMY_ACCESS_TOKEN
                )

                // assert
                Assertions.assertTrue(result.leftOrNull() is Failure.UnauthorizedFailure)
            }

        @Test
        fun `by non-existent id then should return (404 Not Found)`() = runTest {
            // arrange
            val userProfileRemoteSource = UserProfileRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyUserAccount404ResponseJson,
                        status = HttpStatusCode.BadRequest,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = userProfileRemoteSource.getUserProfile(
                userAccountId = DUMMY_USER_ACCOUNT_ID,
                accessToken = DUMMY_ACCESS_TOKEN
            )

            // assert
            Assertions.assertEquals(
                expectedUserAccount404Response.value.message,
                result.leftOrNull()?.message
            )
        }

        @Test
        fun `by valid id but server got internal error then should return (500 Internal Server Error)`() =
            runTest {
                // arrange
                val userProfileRemoteSource = UserProfileRemoteSourceImpl(
                    baseApiUrlLocalSource = baseApiUrlLocalSource,
                    client = mockHandler {
                        respond(
                            content = dummyInternalServerErrorResponseJson,
                            status = HttpStatusCode.InternalServerError,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    })

                // act
                val result = userProfileRemoteSource.getUserProfile(
                    userAccountId = DUMMY_USER_ACCOUNT_ID,
                    accessToken = DUMMY_ACCESS_TOKEN
                )
                // assert
                Assertions.assertEquals(
                    expectedInternalServerErrorResponse.value.message,
                    result.leftOrNull()?.message
                )
                verify(exactly = 1) { baseApiUrlLocalSource.baseApiUrl }
            }
    }

    @Nested
    @DisplayName("when get daily nutrients")
    inner class GetDailyNutrients {
        @Test
        fun `by valid id then should return (200 OK)`() = runTest {
            // arrange
            val userProfileRemoteSource = UserProfileRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyNutrient200ResponseJson,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = userProfileRemoteSource.getDailyNutrients(
                accessToken = DUMMY_ACCESS_TOKEN,
                userId = DUMMY_USER_ACCOUNT_ID,
                date = DUMMY_DATE
            )

            // assert
            Assertions.assertEquals(expectedNutrient200Response, result)
        }

        @Test
        fun `by non-existent id then should return (404 Not Found)`() = runTest {
            // arrange
            val userProfileRemoteSource = UserProfileRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyUserAccount404ResponseJson,
                        status = HttpStatusCode.BadRequest,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = userProfileRemoteSource.getDailyNutrients(
                accessToken = DUMMY_ACCESS_TOKEN,
                userId = DUMMY_USER_ACCOUNT_ID,
                date = DUMMY_DATE
            )

            // assert
            Assertions.assertEquals(
                expectedUserAccount404Response.value.message,
                result.leftOrNull()?.message
            )
        }

        @Test
        fun `by valid id but access token has expired then should return (401 Unauthorized)`() =
            runTest {
                // arrange
                val userProfileRemoteSource = UserProfileRemoteSourceImpl(
                    baseApiUrlLocalSource = baseApiUrlLocalSource,
                    client = mockHandler {
                        respond(
                            content = dummyExpiredAccessTokenResponseJson,
                            status = HttpStatusCode.Unauthorized,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    })

                // act
                val result = userProfileRemoteSource.getDailyNutrients(
                    accessToken = DUMMY_ACCESS_TOKEN,
                    userId = DUMMY_USER_ACCOUNT_ID,
                    date = DUMMY_DATE
                )
                // assert
                Assertions.assertTrue(result.leftOrNull() is Failure.UnauthorizedFailure)
            }


        @Test
        fun `by valid id but server got internal error then should return (500 Internal Server Error)`() =
            runTest {
                // arrange
                val userProfileRemoteSource = UserProfileRemoteSourceImpl(
                    baseApiUrlLocalSource = baseApiUrlLocalSource,
                    client = mockHandler {
                        respond(
                            content = dummyInternalServerErrorResponseJson,
                            status = HttpStatusCode.InternalServerError,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    })

                // act
                val result = userProfileRemoteSource.getDailyNutrients(
                    accessToken = DUMMY_ACCESS_TOKEN,
                    userId = DUMMY_USER_ACCOUNT_ID,
                    date = DUMMY_DATE
                )

                // assert
                Assertions.assertEquals(
                    expectedInternalServerErrorResponse.value.message,
                    result.leftOrNull()?.message
                )
                verify(exactly = 1) { baseApiUrlLocalSource.baseApiUrl }
            }
    }

    @Nested
    @DisplayName("when update diet progress")
    inner class UpdateDietProgress {
        @Test
        fun `by valid body request then should return (200 OK)`() = runTest {
            // arrange
            val userProfileRemoteSource = UserProfileRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyUpdateDietProgress200ResponseJson,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = userProfileRemoteSource.updateDietProgress(
                accessToken = DUMMY_ACCESS_TOKEN,
                dietProgressBodyRequest = dummyDietProgressBodyRequest
            )

            // assert
            Assertions.assertTrue(result.isRight())
        }

        @Test
        fun `by non-existent id then should return (404 Not Found)`() = runTest {
            // arrange
            val userProfileRemoteSource = UserProfileRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyUserAccount404ResponseJson,
                        status = HttpStatusCode.BadRequest,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = userProfileRemoteSource.updateDietProgress(
                accessToken = DUMMY_ACCESS_TOKEN,
                dietProgressBodyRequest = dummyDietProgressBodyRequest
            )

            // assert
            Assertions.assertEquals(
                expectedUserAccount404Response.value.message,
                result.leftOrNull()?.message
            )
        }

        @Test
        fun `by valid id but access token has expired then should return (401 Unauthorized)`() =
            runTest {
                // arrange
                val userProfileRemoteSource = UserProfileRemoteSourceImpl(
                    baseApiUrlLocalSource = baseApiUrlLocalSource,
                    client = mockHandler {
                        respond(
                            content = dummyExpiredAccessTokenResponseJson,
                            status = HttpStatusCode.Unauthorized,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    })

                // act
                val result = userProfileRemoteSource.updateDietProgress(
                    accessToken = DUMMY_ACCESS_TOKEN,
                    dietProgressBodyRequest = dummyDietProgressBodyRequest
                )
                // assert
                Assertions.assertTrue(result.leftOrNull() is Failure.UnauthorizedFailure)
            }


        @Test
        fun `by valid id but server got internal error then should return (500 Internal Server Error)`() =
            runTest {
                // arrange
                val userProfileRemoteSource = UserProfileRemoteSourceImpl(
                    baseApiUrlLocalSource = baseApiUrlLocalSource,
                    client = mockHandler {
                        respond(
                            content = dummyInternalServerErrorResponseJson,
                            status = HttpStatusCode.InternalServerError,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    })

                // act
                val result = userProfileRemoteSource.updateDietProgress(
                    accessToken = DUMMY_ACCESS_TOKEN,
                    dietProgressBodyRequest = dummyDietProgressBodyRequest
                )

                // assert
                Assertions.assertEquals(
                    expectedInternalServerErrorResponse.value.message,
                    result.leftOrNull()?.message
                )
                verify(exactly = 1) { baseApiUrlLocalSource.baseApiUrl }
            }
    }

    @Nested
    @DisplayName("when get diet progress")
    inner class GetDietProgress {
        @Test
        fun `by valid id then should return (200 OK)`() = runTest {
            // arrange
            val userProfileRemoteSource = UserProfileRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyGetDietProgress200ResponseJson,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = userProfileRemoteSource.getDietProgress(
                accessToken = DUMMY_ACCESS_TOKEN,
                userAccountId = DUMMY_USER_ACCOUNT_ID,
            )

            // assert
            Assertions.assertEquals(2, result.getOrNull()?.size)
            Assertions.assertEquals(expectedGetDietProgress200Response, result)
        }

        @Test
        fun `by non-existent id then should return (404 Not Found)`() = runTest {
            // arrange
            val userProfileRemoteSource = UserProfileRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyUserAccount404ResponseJson,
                        status = HttpStatusCode.BadRequest,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = userProfileRemoteSource.getDietProgress(
                accessToken = DUMMY_ACCESS_TOKEN,
                userAccountId = DUMMY_USER_ACCOUNT_ID,
            )

            // assert
            Assertions.assertEquals(
                expectedUserAccount404Response.value.message,
                result.leftOrNull()?.message
            )
        }

        @Test
        fun `by valid id but access token has expired then should return (401 Unauthorized)`() =
            runTest {
                // arrange
                val userProfileRemoteSource = UserProfileRemoteSourceImpl(
                    baseApiUrlLocalSource = baseApiUrlLocalSource,
                    client = mockHandler {
                        respond(
                            content = dummyExpiredAccessTokenResponseJson,
                            status = HttpStatusCode.Unauthorized,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    })

                // act
                val result = userProfileRemoteSource.getDietProgress(
                    accessToken = DUMMY_ACCESS_TOKEN,
                    userAccountId = DUMMY_USER_ACCOUNT_ID,
                )

                // assert
                Assertions.assertTrue(result.leftOrNull() is Failure.UnauthorizedFailure)
            }


        @Test
        fun `by valid id but server got internal error then should return (500 Internal Server Error)`() =
            runTest {
                // arrange
                val userProfileRemoteSource = UserProfileRemoteSourceImpl(
                    baseApiUrlLocalSource = baseApiUrlLocalSource,
                    client = mockHandler {
                        respond(
                            content = dummyInternalServerErrorResponseJson,
                            status = HttpStatusCode.InternalServerError,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    })

                // act
                val result = userProfileRemoteSource.getDietProgress(
                    accessToken = DUMMY_ACCESS_TOKEN,
                    userAccountId = DUMMY_USER_ACCOUNT_ID,
                )

                // assert
                Assertions.assertEquals(
                    expectedInternalServerErrorResponse.value.message,
                    result.leftOrNull()?.message
                )
                verify(exactly = 1) { baseApiUrlLocalSource.baseApiUrl }
            }
    }
}