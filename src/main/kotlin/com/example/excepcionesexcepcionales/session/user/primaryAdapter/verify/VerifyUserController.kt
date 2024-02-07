package com.example.excepcionesexcepcionales.session.user.primaryAdapter.verify

import com.example.excepcionesexcepcionales.session.user.application.find.UserDoesNotExistException
import com.example.excepcionesexcepcionales.session.user.application.verify.VerifyUserCommand
import com.example.excepcionesexcepcionales.session.user.application.verify.VerifyUserCommandHandler
import com.example.excepcionesexcepcionales.session.user.application.verify.VerifyUserError
import com.example.excepcionesexcepcionales.session.user.application.verify.VerifyUserError.InvalidIdentifier
import com.example.excepcionesexcepcionales.session.user.application.verify.VerifyUserError.UserDoesNotExist
import com.example.excepcionesexcepcionales.session.user.application.verify.VerifyUserError.DomainError.CardStatusNotConfirmed
import com.example.excepcionesexcepcionales.session.user.application.verify.VerifyUserError.DomainError.DocumentsNotVerified
import com.example.excepcionesexcepcionales.session.user.application.verify.VerifyUserError.DomainError.UserAlreadyVerified
import com.example.excepcionesexcepcionales.session.user.application.verify.VerifyUserError.DomainError.UserIsIncomplete
import com.example.excepcionesexcepcionales.session.user.domain.User.CardStatusNotConfirmedException
import com.example.excepcionesexcepcionales.session.user.domain.User.NotAllDocumentVerifiedException
import com.example.excepcionesexcepcionales.session.user.domain.User.UserAlreadyVerifiedException
import com.example.excepcionesexcepcionales.session.user.domain.User.UserStatusCannotBeVerifiedException
import com.example.excepcionesexcepcionales.shared.error.Response
import com.example.excepcionesexcepcionales.shared.error.toServerResponse
import com.example.excepcionesexcepcionales.shared.error.withoutBody
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.DOCUMENTS_NOT_VERIFIED
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.INCOMPLETE_USER
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.INVALID_USER_IDENTIFIER
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.PAYMENT_METHOD_NOT_CONFIRMED
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.USER_ALREADY_EXISTS
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.USER_ALREADY_VERIFIED
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.USER_DOES_NOT_EXIST
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestController
class VerifyUserController(
    private val handler: VerifyUserCommandHandler
) {

    @PatchMapping("/users/{userId}/verify")
    @ResponseStatus(OK)
    fun verify(@PathVariable userId: String): Response<*> =
        handler.handle(VerifyUserCommand(userId))
            .toServerResponse(
                onValidResponse = { Response.status(OK).withoutBody() },
                onError = { error -> error.toServerError() }
            )
    
    private fun VerifyUserError.toServerError(): Response<*> = 
        when(this) {
            InvalidIdentifier -> Response.status(BAD_REQUEST).body(INVALID_USER_IDENTIFIER)
            UserDoesNotExist -> Response.status(NOT_FOUND).body(USER_DOES_NOT_EXIST)
            CardStatusNotConfirmed -> Response.status(UNPROCESSABLE_ENTITY).body(PAYMENT_METHOD_NOT_CONFIRMED)
            DocumentsNotVerified -> Response.status(UNPROCESSABLE_ENTITY).body(DOCUMENTS_NOT_VERIFIED)
            UserAlreadyVerified -> Response.status(UNPROCESSABLE_ENTITY).body(USER_ALREADY_VERIFIED)
            UserIsIncomplete -> Response.status(UNPROCESSABLE_ENTITY).body(INCOMPLETE_USER)
        }

    @RestControllerAdvice(assignableTypes = [VerifyUserController::class])
    class VerifyUserControllerExceptionHandler {
        @ExceptionHandler(value = [UserDoesNotExistException::class])
        @ResponseStatus(UNPROCESSABLE_ENTITY)
        fun handleError(e: UserDoesNotExistException) = USER_ALREADY_EXISTS

        @ExceptionHandler(value = [IllegalArgumentException::class])
        @ResponseStatus(BAD_REQUEST)
        fun handleError(e: IllegalArgumentException) = INVALID_USER_IDENTIFIER

        @ExceptionHandler(value = [NotAllDocumentVerifiedException::class])
        @ResponseStatus(UNPROCESSABLE_ENTITY)
        fun handleError(e: NotAllDocumentVerifiedException) = DOCUMENTS_NOT_VERIFIED

        @ExceptionHandler(value = [UserStatusCannotBeVerifiedException::class])
        @ResponseStatus(UNPROCESSABLE_ENTITY)
        fun handleError(e: UserStatusCannotBeVerifiedException) = INCOMPLETE_USER

        @ExceptionHandler(value = [UserAlreadyVerifiedException::class])
        @ResponseStatus(UNPROCESSABLE_ENTITY)
        fun handleError(e: UserAlreadyVerifiedException) = USER_ALREADY_VERIFIED

        @ExceptionHandler(value = [CardStatusNotConfirmedException::class])
        @ResponseStatus(UNPROCESSABLE_ENTITY)
        fun handleError(e: CardStatusNotConfirmedException) = PAYMENT_METHOD_NOT_CONFIRMED
    }
}
