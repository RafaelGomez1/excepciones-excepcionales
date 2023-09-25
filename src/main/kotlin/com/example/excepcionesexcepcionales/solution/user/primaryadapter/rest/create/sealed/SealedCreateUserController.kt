package com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.sealed

import com.example.excepcionesexcepcionales.solution.user.application.create.sealed.CreateUserResult
import com.example.excepcionesexcepcionales.solution.user.application.create.sealed.CreateUserResult.InvalidEmail
import com.example.excepcionesexcepcionales.solution.user.application.create.sealed.CreateUserResult.InvalidMobilePhone
import com.example.excepcionesexcepcionales.solution.user.application.create.sealed.CreateUserResult.InvalidName
import com.example.excepcionesexcepcionales.solution.user.application.create.sealed.CreateUserResult.InvalidSurname
import com.example.excepcionesexcepcionales.solution.user.application.create.sealed.CreateUserResult.Success
import com.example.excepcionesexcepcionales.solution.user.application.create.sealed.CreateUserResult.Unknown
import com.example.excepcionesexcepcionales.solution.user.application.create.sealed.CreateUserResult.UserAlreadyExists
import com.example.excepcionesexcepcionales.solution.user.application.create.sealed.SealedCreateUserCommand
import com.example.excepcionesexcepcionales.solution.user.application.create.sealed.SealedCreateUserCommandHandler
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.CreateUserRequestBody
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.INVALID_EMAIL
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.INVALID_NAME
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.INVALID_PHONE_NUMBER
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.INVALID_SURNAME
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.USER_ALREADY_EXISTS
import java.time.ZonedDateTime
import java.util.UUID
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SealedCreateUserController(private val handler: SealedCreateUserCommandHandler) {

    @PostMapping("/imperative/users")
    fun create(@RequestBody body: CreateUserRequestBody): ResponseEntity<*> =
        with(body) {
            handler.handle(
                SealedCreateUserCommand(
                    id = UUID.randomUUID(),
                    email = email,
                    phonePrefix = phonePrefix,
                    phoneNumber = phoneNumber,
                    name = name,
                    surname = surname,
                    createdOn = ZonedDateTime.now()
                )
            ).toServerError()
        }

    private fun CreateUserResult.toServerError(): ResponseEntity<*> =
        when(this) {
            is InvalidEmail -> ResponseEntity.badRequest().body(INVALID_EMAIL)
            is InvalidMobilePhone -> ResponseEntity.badRequest().body(INVALID_PHONE_NUMBER)
            is InvalidName -> ResponseEntity.badRequest().body(INVALID_NAME)
            is InvalidSurname -> ResponseEntity.badRequest().body(INVALID_SURNAME)
            is UserAlreadyExists -> ResponseEntity.status(CONFLICT).body(USER_ALREADY_EXISTS)
            is Success -> ResponseEntity.status(CREATED).body(null)
            is Unknown -> throw error
        }
}
