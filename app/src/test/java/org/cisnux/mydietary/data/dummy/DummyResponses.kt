package org.cisnux.mydietary.data.dummy

import arrow.core.Either
import org.cisnux.mydietary.data.remotes.bodyrequests.UserAccountBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.VerifyEmailBodyRequest
import org.cisnux.mydietary.data.remotes.responses.AddedUserAccountResponse
import org.cisnux.mydietary.utils.Failure

val dummyUserAccountBodyRequest = UserAccountBodyRequest(
    emailAddress = "test@example.com",
    password = "123",
)

val dummyVerifyEmailBodyRequest = VerifyEmailBodyRequest(
    emailAddress = "test@example.com"
)

val dummyAddedUserAccount200ResponseJson = """
    {
        "message": "success",
        "data": {
            "id": "1",
            "email": "test@example.com",
            "appToken": "123"
        } 
    }
""".trimIndent()

val dummyVerifyEmail200ResponseJson = """
    {
        "message": "success",
        "data": "the verification email has been sent"
    }
""".trimIndent()


val dummyAddedUserAccountIncorrectPasswordResponseJson = """
    {
        "message": "the password is incorrect",
        "data": null
    }
""".trimIndent()

val dummyAddedUserAccountIncorrectEmailAddressResponseJson = """
    {
        "message": "the email address is incorrect",
        "data": null
    }
""".trimIndent()

val dummyVerifyEmailInternalServerErrorResponseJson = """
    {
        "message": "our server has encountered an error",
        "data": null
    }
""".trimIndent()

val expectedAddedUserAccount200Response = Either.Right(
    AddedUserAccountResponse(
        id = "1",
        emailAddress = "test@example.com",
        accessToken = "123"
    )
)

val expectedAddedUserAccountIncorrectPasswordResponse = Either.Left(
    Failure.UnauthorizedFailure(message = "the password is incorrect")
)

val expectedAddedUserAccountIncorrectEmailAddressResponse = Either.Left(
    Failure.UnauthorizedFailure(message = "the email address is incorrect")
)

val expectedVerifyEmailInternalServerErrorResponse = Either.Left(
    Failure.UnauthorizedFailure(message = "our server has encountered an error")
)

val expectedVerifyEmail200Response = Either.Right("the verification email has been sent")

const val DUMMY_ACCESS_TOKEN = "123"