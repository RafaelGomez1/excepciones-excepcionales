package com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.functional

import com.example.excepcionesexcepcionales.shared.error.Response
import com.example.excepcionesexcepcionales.shared.error.toServerResponse
import com.example.excepcionesexcepcionales.shared.error.withoutBody
import com.example.excepcionesexcepcionales.solution.user.application.verify.functional.FunctionalVerifyUserCommand
import com.example.excepcionesexcepcionales.solution.user.application.verify.functional.FunctionalVerifyUserCommandHandler
import com.example.excepcionesexcepcionales.solution.user.application.verify.functional.FunctionalVerifyUserError
import com.example.excepcionesexcepcionales.solution.user.application.verify.functional.FunctionalVerifyUserError.AggregateError
import com.example.excepcionesexcepcionales.solution.user.application.verify.functional.FunctionalVerifyUserError.UserNotFound
import com.example.excepcionesexcepcionales.solution.user.domain.VerifyUserDomainError
import com.example.excepcionesexcepcionales.solution.user.domain.VerifyUserDomainError.CardStatusNotConfirmed
import com.example.excepcionesexcepcionales.solution.user.domain.VerifyUserDomainError.NotAllDocumentVerified
import com.example.excepcionesexcepcionales.solution.user.domain.VerifyUserDomainError.UserAlreadyVerified
import com.example.excepcionesexcepcionales.solution.user.domain.VerifyUserDomainError.UserStatusCannotBeVerified
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.DOCUMENTS_NOT_VERIFIED
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.INCOMPLETE_USER
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.PAYMENT_METHOD_NOT_CONFIRMED
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.USER_ALREADY_VERIFIED
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.USER_DOES_NOT_EXIST
import java.util.*
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class FunctionalVerifyUserController(
    private val handler: FunctionalVerifyUserCommandHandler
) {

    @PatchMapping("/users/{userId}/functional-verify")
    fun verify(@PathVariable userId: String): Response<*> =
        handler.handle(
            FunctionalVerifyUserCommand(userId = UUID.fromString(userId))
        )
            .toServerResponse(
                onError = { error -> error.toServerError() },
                onValidResponse = { Response.status(CREATED).withoutBody() }
            )
}

private fun FunctionalVerifyUserError.toServerError(): Response<*> =
    when(this) {
        UserNotFound -> Response.status(BAD_REQUEST).body(USER_DOES_NOT_EXIST)
        is AggregateError -> error.toServerError()
    }

private fun VerifyUserDomainError.toServerError(): Response<*> =
    when(this) {
        CardStatusNotConfirmed -> Response.status(UNPROCESSABLE_ENTITY).body(PAYMENT_METHOD_NOT_CONFIRMED)
        NotAllDocumentVerified -> Response.status(UNPROCESSABLE_ENTITY).body(DOCUMENTS_NOT_VERIFIED)
        UserAlreadyVerified -> Response.status(UNPROCESSABLE_ENTITY).body(USER_ALREADY_VERIFIED)
        UserStatusCannotBeVerified -> Response.status(UNPROCESSABLE_ENTITY).body(INCOMPLETE_USER)
    }
