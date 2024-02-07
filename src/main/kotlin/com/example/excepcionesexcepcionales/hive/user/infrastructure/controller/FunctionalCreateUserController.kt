package com.example.excepcionesexcepcionales.hive.user.infrastructure.controller

import com.example.excepcionesexcepcionales.hive.user.application.create.fp.CreateUserError
import com.example.excepcionesexcepcionales.hive.user.application.create.fp.CreateUserError.*
import com.example.excepcionesexcepcionales.hive.user.application.create.fp.CreateUserRequest
import com.example.excepcionesexcepcionales.hive.user.application.create.fp.HiveCreateUserMediator
import com.example.excepcionesexcepcionales.hive.user.infrastructure.controller.ServerErrors.INVALID_EMAIL
import com.example.excepcionesexcepcionales.hive.user.infrastructure.controller.ServerErrors.INVALID_ROLE
import com.example.excepcionesexcepcionales.hive.user.infrastructure.controller.ServerErrors.ROLE_NOT_ALLOWED
import com.example.excepcionesexcepcionales.hive.user.infrastructure.controller.ServerErrors.USER_ALREADY_EXISTS
import com.example.excepcionesexcepcionales.shared.error.Response
import com.example.excepcionesexcepcionales.shared.error.toServerResponse
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
class FunctionalCreateUserController(private val createUser: HiveCreateUserMediator) {

    @PostMapping(value = ["/user/functional"], produces = [APPLICATION_JSON_VALUE])
    @ResponseBody
    fun execute(@RequestBody(required = true) userRequest: UserRequest, creator: String): Response<*> =
        createUser(
            CreateUserRequest(
                name = userRequest.name,
                email = userRequest.email,
                role = userRequest.role ?: "BASIC",
                creator = creator // Hardcoded value for a simplified example
            )
        ).toServerResponse(
            onValidResponse = { Response.status(CREATED).body("{}") },
            onError = { error -> error.toServerError() }
        )

    private fun CreateUserError.toServerError(): Response<*> =
        when(this) {
            InvalidEmail -> Response.status(BAD_REQUEST).body(INVALID_EMAIL)
            InvalidRole -> Response.status(BAD_REQUEST).body(INVALID_ROLE)
            UserAlreadyExists -> Response.status(BAD_REQUEST).body(USER_ALREADY_EXISTS)
            UserRoleNotAllowed -> Response.status(BAD_REQUEST).body(ROLE_NOT_ALLOWED)
        }
}
