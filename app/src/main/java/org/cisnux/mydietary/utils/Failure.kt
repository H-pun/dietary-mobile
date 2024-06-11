package org.cisnux.mydietary.utils

import io.ktor.http.HttpStatusCode

sealed class Failure(override var message: String?) : Exception(message) {
    sealed class HttpFailure(
        override var message: String? = null,
    ) : Failure(message)

    data class ConnectionFailure(
        override var message: String? = "😞No internet connection",
    ) : HttpFailure(message)

    data class NotFoundFailure(
        override var message: String? = null,
    ) : HttpFailure(message)

    data class ConflictFailure(
        override var message: String? = null,
    ) : HttpFailure(message)

    data class ServerFailure(
        override var message: String? = null,
    ) : HttpFailure(message)

    class BadRequestFailure(
        override var message: String? = null,
    ) : HttpFailure(message)

    class UnauthorizedFailure(
        override var message: String? = null,
    ) : HttpFailure(message)

    companion object {
        val HTTP_FAILURES = mapOf(
            HttpStatusCode.Conflict to ConflictFailure(),
            HttpStatusCode.BadRequest to BadRequestFailure(),
            HttpStatusCode.Unauthorized to UnauthorizedFailure(),
            HttpStatusCode.NotFound to NotFoundFailure(),
            HttpStatusCode.InternalServerError to ServerFailure(),
            HttpStatusCode.InternalServerError to ServerFailure(),
            HttpStatusCode.NotImplemented to ServerFailure(),
            HttpStatusCode.BadGateway to ServerFailure(),
            HttpStatusCode.ServiceUnavailable to ServerFailure(),
            HttpStatusCode.GatewayTimeout to ServerFailure(),
            HttpStatusCode.VersionNotSupported to ServerFailure(),
            HttpStatusCode.VariantAlsoNegotiates to ServerFailure(),
            HttpStatusCode.InsufficientStorage to ServerFailure()
        )
    }
}