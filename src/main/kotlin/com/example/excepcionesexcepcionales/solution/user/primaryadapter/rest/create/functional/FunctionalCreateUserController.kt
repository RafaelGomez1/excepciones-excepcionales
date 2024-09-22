package com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.functional

import com.example.excepcionesexcepcionales.shared.clock.Clock
import com.example.excepcionesexcepcionales.shared.error.Response
import com.example.excepcionesexcepcionales.shared.error.toServerResponse
import com.example.excepcionesexcepcionales.shared.error.withoutBody
import com.example.excepcionesexcepcionales.shared.id.IdGenerator
import com.example.excepcionesexcepcionales.solution.user.application.create.functional.CreateUserError
import com.example.excepcionesexcepcionales.solution.user.application.create.functional.CreateUserError.*
import com.example.excepcionesexcepcionales.solution.user.application.create.functional.FunctionalCreateUserCommand
import com.example.excepcionesexcepcionales.solution.user.application.create.functional.FunctionalCreateUserCommandHandler
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.CreateUserRequestBody
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.INVALID_EMAIL
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.INVALID_NAME
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.INVALID_PHONE_NUMBER
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.INVALID_SURNAME
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.USER_ALREADY_EXISTS
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.CREATED
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class FunctionalCreateUserController(
    private val handler: FunctionalCreateUserCommandHandler,
    private val idGenerator: IdGenerator,
    private val clock: Clock
) {

    @PostMapping("/functional/users")
    fun create(@RequestBody body: CreateUserRequestBody): Response<*> =
        with(body) {
            handler.handle(
                FunctionalCreateUserCommand(
                    id = idGenerator.generate(),
                    email = email,
                    phonePrefix = phonePrefix,
                    phoneNumber = phoneNumber,
                    name = name,
                    surname = surname,
                    createdOn = clock.now()
                )
            ).toServerResponse(
                onError = { error -> error.toServerError() },
                onValidResponse = { Response.status(CREATED).withoutBody() }
            )
        }

    private fun CreateUserError.toServerError(): Response<*> =
        when(this) {
            is InvalidEmail -> Response.badRequest().body(INVALID_EMAIL)
            is InvalidMobilePhone -> Response.badRequest().body(INVALID_PHONE_NUMBER)
            is InvalidName -> Response.badRequest().body(INVALID_NAME)
            is InvalidSurname -> Response.badRequest().body(INVALID_SURNAME)
            is UserAlreadyExists -> Response.status(CONFLICT).body(USER_ALREADY_EXISTS)
        }
}
