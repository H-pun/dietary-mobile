package org.cisnux.mydietary.data.remotes

import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.cisnux.mydietary.commons.utils.DUMMY_ACCESS_TOKEN
import org.cisnux.mydietary.commons.utils.DUMMY_FOOD_DIARY_ID
import org.cisnux.mydietary.commons.utils.Failure
import org.cisnux.mydietary.commons.utils.dummyAddedFoodDiary201ResponseJson
import org.cisnux.mydietary.commons.utils.dummyDeleteFoodDiaryResponse200Json
import org.cisnux.mydietary.commons.utils.dummyDetected200ResponseJson
import org.cisnux.mydietary.commons.utils.dummyExpiredAccessTokenResponseJson
import org.cisnux.mydietary.commons.utils.dummyFile
import org.cisnux.mydietary.commons.utils.dummyFoodDiaryBodyRequest
import org.cisnux.mydietary.commons.utils.dummyGetFoodDiaries200ResponseJson
import org.cisnux.mydietary.commons.utils.dummyFoodDiary404ResponseJson
import org.cisnux.mydietary.commons.utils.dummyGetFoodDiaryParams
import org.cisnux.mydietary.commons.utils.dummyGetFoodDiaryReports200ResponseJson
import org.cisnux.mydietary.commons.utils.dummyGetFoodDiaryResponse200Json
import org.cisnux.mydietary.commons.utils.dummyInternalServerErrorResponseJson
import org.cisnux.mydietary.commons.utils.dummyReportBodyRequest
import org.cisnux.mydietary.commons.utils.dummyUserAccount404ResponseJson
import org.cisnux.mydietary.commons.utils.expectedAddedFoodDiary201Response
import org.cisnux.mydietary.commons.utils.expectedDetected200Response
import org.cisnux.mydietary.commons.utils.expectedGetFoodDiaries200Response
import org.cisnux.mydietary.commons.utils.expectedGetFoodDiary200Response
import org.cisnux.mydietary.commons.utils.expectedFoodDiary404Response
import org.cisnux.mydietary.commons.utils.expectedGetFoodDiaryReports200Response
import org.cisnux.mydietary.commons.utils.expectedGetSuggestionKeywords200Response
import org.cisnux.mydietary.commons.utils.expectedInternalServerErrorResponse
import org.cisnux.mydietary.commons.utils.expectedUserAccount404Response
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
@DisplayName("Food Diary")
class FoodDiaryRemoteSourceTest : BaseRemoteTest() {
    @Nested
    @DisplayName("when get food diaries")
    inner class GetFoodDiaries {

        @Test
        fun `by valid params then should return (200 OK)`() = runTest {
            // arrange
            val foodDiaryRemoteSource = FoodDiaryRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyGetFoodDiaries200ResponseJson,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = foodDiaryRemoteSource.getFoodDiaries(
                accessToken = DUMMY_ACCESS_TOKEN,
                getFoodDiaryParams = dummyGetFoodDiaryParams
            )

            // assert
            Assertions.assertEquals(2, result.getOrNull()?.size)
            Assertions.assertEquals(expectedGetFoodDiaries200Response, result)
        }

        @Test
        fun `by non-existent user account's id then should return (404 Not Found)`() = runTest {
            // arrange
            val foodDiaryRemoteSource = FoodDiaryRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyUserAccount404ResponseJson,
                        status = HttpStatusCode.NotFound,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = foodDiaryRemoteSource.getFoodDiaries(
                accessToken = DUMMY_ACCESS_TOKEN,
                getFoodDiaryParams = dummyGetFoodDiaryParams
            )

            // assert
            Assertions.assertEquals(
                expectedUserAccount404Response.value.message,
                result.leftOrNull()?.message
            )
        }

        @Test
        fun `by valid params but access token has expired then should return (401 Unauthorized)`() =
            runTest {
                // arrange
                val foodDiaryRemoteSource = FoodDiaryRemoteSourceImpl(
                    baseApiUrlLocalSource = baseApiUrlLocalSource,
                    client = mockHandler {
                        respond(
                            content = dummyExpiredAccessTokenResponseJson,
                            status = HttpStatusCode.Unauthorized,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    })

                // act
                val result = foodDiaryRemoteSource.getFoodDiaries(
                    accessToken = DUMMY_ACCESS_TOKEN,
                    getFoodDiaryParams = dummyGetFoodDiaryParams
                )

                // assert
                Assertions.assertTrue(result.leftOrNull() is Failure.UnauthorizedFailure)
            }

        @Test
        fun `by valid params but server got internal error then should return (500 Internal Server Error)`() =
            runTest {
                // arrange
                val foodDiaryRemoteSource = FoodDiaryRemoteSourceImpl(
                    baseApiUrlLocalSource = baseApiUrlLocalSource,
                    client = mockHandler {
                        respond(
                            content = dummyInternalServerErrorResponseJson,
                            status = HttpStatusCode.InternalServerError,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    })

                // act
                val result = foodDiaryRemoteSource.getFoodDiaries(
                    accessToken = DUMMY_ACCESS_TOKEN,
                    getFoodDiaryParams = dummyGetFoodDiaryParams
                )

                // assert
                Assertions.assertEquals(
                    expectedInternalServerErrorResponse.value.message,
                    result.leftOrNull()?.message
                )
            }
    }

    @Nested
    @DisplayName("when get food diary")
    inner class GetFoodDiary {

        @Test
        fun `by valid food diary's id then should return (200 OK)`() = runTest {
            // arrange
            val foodDiaryRemoteSource = FoodDiaryRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyGetFoodDiaryResponse200Json,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = foodDiaryRemoteSource.getFoodDiaryById(
                accessToken = DUMMY_ACCESS_TOKEN,
                id = DUMMY_FOOD_DIARY_ID
            )

            // assert
            Assertions.assertEquals(expectedGetFoodDiary200Response, result)
        }

        @Test
        fun `by non-existent food diary's id then should return (404 Not Found)`() = runTest {
            // arrange
            val foodDiaryRemoteSource = FoodDiaryRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyFoodDiary404ResponseJson,
                        status = HttpStatusCode.NotFound,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = foodDiaryRemoteSource.getFoodDiaryById(
                accessToken = DUMMY_ACCESS_TOKEN,
                id = DUMMY_FOOD_DIARY_ID
            )

            // assert
            Assertions.assertEquals(
                expectedFoodDiary404Response.value.message,
                result.leftOrNull()?.message
            )
        }

        @Test
        fun `by valid food diary's id but access token has expired then should return (401 Unauthorized)`() =
            runTest {
                // arrange
                val foodDiaryRemoteSource = FoodDiaryRemoteSourceImpl(
                    baseApiUrlLocalSource = baseApiUrlLocalSource,
                    client = mockHandler {
                        respond(
                            content = dummyExpiredAccessTokenResponseJson,
                            status = HttpStatusCode.Unauthorized,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    })

                // act
                val result = foodDiaryRemoteSource.getFoodDiaryById(
                    accessToken = DUMMY_ACCESS_TOKEN,
                    id = DUMMY_FOOD_DIARY_ID
                )

                // assert
                Assertions.assertTrue(result.leftOrNull() is Failure.UnauthorizedFailure)
            }

        @Test
        fun `by valid food diary's id but server got internal error then should return (500 Internal Server Error)`() =
            runTest {
                // arrange
                val foodDiaryRemoteSource = FoodDiaryRemoteSourceImpl(
                    baseApiUrlLocalSource = baseApiUrlLocalSource,
                    client = mockHandler {
                        respond(
                            content = dummyInternalServerErrorResponseJson,
                            status = HttpStatusCode.InternalServerError,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    })

                // act
                val result = foodDiaryRemoteSource.getFoodDiaryById(
                    accessToken = DUMMY_ACCESS_TOKEN,
                    id = DUMMY_FOOD_DIARY_ID
                )

                // assert
                Assertions.assertEquals(
                    expectedInternalServerErrorResponse.value.message,
                    result.leftOrNull()?.message
                )
            }
    }

    @Nested
    @DisplayName("when add food diary")
    inner class AddFoodDiary {

        @Test
        fun `by valid body request then should return (201 Created)`() = runTest {
            // arrange
            val foodDiaryRemoteSource = FoodDiaryRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyAddedFoodDiary201ResponseJson,
                        status = HttpStatusCode.Created,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = foodDiaryRemoteSource.addFoodDiary(
                accessToken = DUMMY_ACCESS_TOKEN,
                foodDiary = dummyFoodDiaryBodyRequest
            )

            // assert
            Assertions.assertEquals(expectedAddedFoodDiary201Response, result)
        }

        @Test
        fun `by non-existent user account's id then should return (404 Not Found)`() = runTest {
            // arrange
            val foodDiaryRemoteSource = FoodDiaryRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyUserAccount404ResponseJson,
                        status = HttpStatusCode.NotFound,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = foodDiaryRemoteSource.addFoodDiary(
                foodDiary = dummyFoodDiaryBodyRequest,
                accessToken = DUMMY_ACCESS_TOKEN
            )

            // assert
            Assertions.assertEquals(
                expectedUserAccount404Response.value.message,
                result.leftOrNull()?.message
            )
        }

        @Test
        fun `by valid body request but access token has expired then should return (401 Unauthorized)`() =
            runTest {
                // arrange
                val foodDiaryRemoteSource = FoodDiaryRemoteSourceImpl(
                    baseApiUrlLocalSource = baseApiUrlLocalSource,
                    client = mockHandler {
                        respond(
                            content = dummyExpiredAccessTokenResponseJson,
                            status = HttpStatusCode.Unauthorized,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    })

                // act
                val result = foodDiaryRemoteSource.addFoodDiary(
                    foodDiary = dummyFoodDiaryBodyRequest,
                    accessToken = DUMMY_ACCESS_TOKEN
                )

                // assert
                Assertions.assertTrue(result.leftOrNull() is Failure.UnauthorizedFailure)
            }

        @Test
        fun `by valid food diary's id but server got internal error then should return (500 Internal Server Error)`() =
            runTest {
                // arrange
                val foodDiaryRemoteSource = FoodDiaryRemoteSourceImpl(
                    baseApiUrlLocalSource = baseApiUrlLocalSource,
                    client = mockHandler {
                        respond(
                            content = dummyInternalServerErrorResponseJson,
                            status = HttpStatusCode.InternalServerError,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    })

                // act
                val result = foodDiaryRemoteSource.addFoodDiary(
                    foodDiary = dummyFoodDiaryBodyRequest,
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
    @DisplayName("when detect foods")
    inner class DetectFoods {

        @Test
        fun `by valid body request then should return (200 OK)`() = runTest {
            // arrange
            val foodDiaryRemoteSource = FoodDiaryRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyDetected200ResponseJson,
                        status = HttpStatusCode.Created,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = foodDiaryRemoteSource.detectFoods(
                accessToken = DUMMY_ACCESS_TOKEN,
                foodPicture = dummyFile
            )

            // assert
            Assertions.assertEquals(expectedDetected200Response, result)
        }

        @Test
        fun `by valid body request but access token has expired then should return (401 Unauthorized)`() =
            runTest {
                // arrange
                val foodDiaryRemoteSource = FoodDiaryRemoteSourceImpl(
                    baseApiUrlLocalSource = baseApiUrlLocalSource,
                    client = mockHandler {
                        respond(
                            content = dummyExpiredAccessTokenResponseJson,
                            status = HttpStatusCode.Unauthorized,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    })

                // act
                val result = foodDiaryRemoteSource.detectFoods(
                    accessToken = DUMMY_ACCESS_TOKEN,
                    foodPicture = dummyFile
                )

                // assert
                Assertions.assertTrue(result.leftOrNull() is Failure.UnauthorizedFailure)
            }

        @Test
        fun `by valid food diary's id but server got internal error then should return (500 Internal Server Error)`() =
            runTest {
                // arrange
                val foodDiaryRemoteSource = FoodDiaryRemoteSourceImpl(
                    baseApiUrlLocalSource = baseApiUrlLocalSource,
                    client = mockHandler {
                        respond(
                            content = dummyInternalServerErrorResponseJson,
                            status = HttpStatusCode.InternalServerError,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    })

                // act
                val result = foodDiaryRemoteSource.detectFoods(
                    accessToken = DUMMY_ACCESS_TOKEN,
                    foodPicture = dummyFile
                )

                // assert
                Assertions.assertEquals(
                    expectedInternalServerErrorResponse.value.message,
                    result.leftOrNull()?.message
                )
            }
    }

    @Nested
    @DisplayName("when get food diary reports")
    inner class GetFoodDiaryReports {

        @Test
        fun `by valid body request then should return (200 OK)`() = runTest {
            // arrange
            val foodDiaryRemoteSource = FoodDiaryRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyGetFoodDiaryReports200ResponseJson,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = foodDiaryRemoteSource.getFoodDiaryReports(
                accessToken = DUMMY_ACCESS_TOKEN,
                reportBodyRequest = dummyReportBodyRequest
            )

            // assert
            Assertions.assertEquals(2, result.getOrNull()?.size)
            Assertions.assertEquals(expectedGetFoodDiaryReports200Response, result)
        }

        @Test
        fun `by non-existent user account's id then should return (404 Not Found)`() = runTest {
            // arrange
            val foodDiaryRemoteSource = FoodDiaryRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyUserAccount404ResponseJson,
                        status = HttpStatusCode.NotFound,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = foodDiaryRemoteSource.getFoodDiaryReports(
                accessToken = DUMMY_ACCESS_TOKEN,
                reportBodyRequest = dummyReportBodyRequest
            )

            // assert
            Assertions.assertEquals(
                expectedUserAccount404Response.value.message,
                result.leftOrNull()?.message
            )
        }

        @Test
        fun `by valid body request but access token has expired then should return (401 Unauthorized)`() =
            runTest {
                // arrange
                val foodDiaryRemoteSource = FoodDiaryRemoteSourceImpl(
                    baseApiUrlLocalSource = baseApiUrlLocalSource,
                    client = mockHandler {
                        respond(
                            content = dummyExpiredAccessTokenResponseJson,
                            status = HttpStatusCode.Unauthorized,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    })

                // act
                val result = foodDiaryRemoteSource.getFoodDiaryReports(
                    accessToken = DUMMY_ACCESS_TOKEN,
                    reportBodyRequest = dummyReportBodyRequest
                )

                // assert
                Assertions.assertTrue(result.leftOrNull() is Failure.UnauthorizedFailure)
            }

        @Test
        fun `by valid body request but server got internal error then should return (500 Internal Server Error)`() =
            runTest {
                // arrange
                val foodDiaryRemoteSource = FoodDiaryRemoteSourceImpl(
                    baseApiUrlLocalSource = baseApiUrlLocalSource,
                    client = mockHandler {
                        respond(
                            content = dummyInternalServerErrorResponseJson,
                            status = HttpStatusCode.InternalServerError,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    })

                // act
                val result = foodDiaryRemoteSource.getFoodDiaryReports(
                    accessToken = DUMMY_ACCESS_TOKEN,
                    reportBodyRequest = dummyReportBodyRequest
                )

                // assert
                Assertions.assertEquals(
                    expectedInternalServerErrorResponse.value.message,
                    result.leftOrNull()?.message
                )
            }
    }

    @Nested
    @DisplayName("when get suggestion keywords")
    inner class GetSuggestionKeywords {

        @Test
        fun `by valid params then should return (200 OK)`() = runTest {
            // arrange
            val foodDiaryRemoteSource = FoodDiaryRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyGetFoodDiaries200ResponseJson,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = foodDiaryRemoteSource.getSuggestionKeywords(
                accessToken = DUMMY_ACCESS_TOKEN,
                getFoodDiaryParams = dummyGetFoodDiaryParams
            )

            // assert
            Assertions.assertEquals(2, result.getOrNull()?.size)
            Assertions.assertEquals(expectedGetSuggestionKeywords200Response, result)
        }

        @Test
        fun `by non-existent user account's id then should return (404 Not Found)`() = runTest {
            // arrange
            val foodDiaryRemoteSource = FoodDiaryRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyUserAccount404ResponseJson,
                        status = HttpStatusCode.NotFound,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = foodDiaryRemoteSource.getSuggestionKeywords(
                accessToken = DUMMY_ACCESS_TOKEN,
                getFoodDiaryParams = dummyGetFoodDiaryParams
            )

            // assert
            Assertions.assertEquals(
                expectedUserAccount404Response.value.message,
                result.leftOrNull()?.message
            )
        }

        @Test
        fun `by valid params but access token has expired then should return (401 Unauthorized)`() =
            runTest {
                // arrange
                val foodDiaryRemoteSource = FoodDiaryRemoteSourceImpl(
                    baseApiUrlLocalSource = baseApiUrlLocalSource,
                    client = mockHandler {
                        respond(
                            content = dummyExpiredAccessTokenResponseJson,
                            status = HttpStatusCode.Unauthorized,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    })

                // act
                val result = foodDiaryRemoteSource.getFoodDiaryReports(
                    accessToken = DUMMY_ACCESS_TOKEN,
                    reportBodyRequest = dummyReportBodyRequest
                )

                // assert
                Assertions.assertTrue(result.leftOrNull() is Failure.UnauthorizedFailure)
            }

        @Test
        fun `by valid params but server got internal error then should return (500 Internal Server Error)`() =
            runTest {
                // arrange
                val foodDiaryRemoteSource = FoodDiaryRemoteSourceImpl(
                    baseApiUrlLocalSource = baseApiUrlLocalSource,
                    client = mockHandler {
                        respond(
                            content = dummyInternalServerErrorResponseJson,
                            status = HttpStatusCode.InternalServerError,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    })

                // act
                val result = foodDiaryRemoteSource.getFoodDiaryReports(
                    accessToken = DUMMY_ACCESS_TOKEN,
                    reportBodyRequest = dummyReportBodyRequest
                )

                // assert
                Assertions.assertEquals(
                    expectedInternalServerErrorResponse.value.message,
                    result.leftOrNull()?.message
                )
            }
    }

    @Nested
    @DisplayName("when delete food diary")
    inner class DeleteFoodDiary {

        @Test
        fun `by valid food diary's id then should return (200 OK)`() = runTest {
            // arrange
            val foodDiaryRemoteSource = FoodDiaryRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyDeleteFoodDiaryResponse200Json,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = foodDiaryRemoteSource.deleteFoodDiaryById(
                accessToken = DUMMY_ACCESS_TOKEN,
                id = DUMMY_FOOD_DIARY_ID
            )

            // assert
            Assertions.assertTrue(result.isRight())
        }

        @Test
        fun `by non-existent food diary's id then should return (404 Not Found)`() = runTest {
            // arrange
            val foodDiaryRemoteSource = FoodDiaryRemoteSourceImpl(
                baseApiUrlLocalSource = baseApiUrlLocalSource,
                client = mockHandler {
                    respond(
                        content = dummyFoodDiary404ResponseJson,
                        status = HttpStatusCode.NotFound,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                })

            // act
            val result = foodDiaryRemoteSource.deleteFoodDiaryById(
                accessToken = DUMMY_ACCESS_TOKEN,
                id = DUMMY_FOOD_DIARY_ID
            )

            // assert
            Assertions.assertEquals(
                expectedFoodDiary404Response.value.message,
                result.leftOrNull()?.message
            )
        }

        @Test
        fun `by valid food diary's id but access token has expired then should return (401 Unauthorized)`() =
            runTest {
                // arrange
                val foodDiaryRemoteSource = FoodDiaryRemoteSourceImpl(
                    baseApiUrlLocalSource = baseApiUrlLocalSource,
                    client = mockHandler {
                        respond(
                            content = dummyExpiredAccessTokenResponseJson,
                            status = HttpStatusCode.Unauthorized,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    })

                // act
                val result = foodDiaryRemoteSource.deleteFoodDiaryById(
                    accessToken = DUMMY_ACCESS_TOKEN,
                    id = DUMMY_FOOD_DIARY_ID
                )

                // assert
                Assertions.assertTrue(result.leftOrNull() is Failure.UnauthorizedFailure)
            }

        @Test
        fun `by valid food diary's id but server got internal error then should return (500 Internal Server Error)`() =
            runTest {
                // arrange
                val foodDiaryRemoteSource = FoodDiaryRemoteSourceImpl(
                    baseApiUrlLocalSource = baseApiUrlLocalSource,
                    client = mockHandler {
                        respond(
                            content = dummyInternalServerErrorResponseJson,
                            status = HttpStatusCode.InternalServerError,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    })

                // act
                val result = foodDiaryRemoteSource.deleteFoodDiaryById(
                    accessToken = DUMMY_ACCESS_TOKEN,
                    id = DUMMY_FOOD_DIARY_ID
                )

                // assert
                Assertions.assertEquals(
                    expectedInternalServerErrorResponse.value.message,
                    result.leftOrNull()?.message
                )
            }
    }
}