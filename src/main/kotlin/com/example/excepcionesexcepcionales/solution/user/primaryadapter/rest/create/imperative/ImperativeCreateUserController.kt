package com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.imperative

import com.example.excepcionesexcepcionales.solution.user.application.create.imperative.ImperativeCreateUserCommand
import com.example.excepcionesexcepcionales.solution.user.application.create.imperative.ImperativeCreateUserCommandHandler
import com.example.excepcionesexcepcionales.solution.user.domain.Email.InvalidEmailException
import com.example.excepcionesexcepcionales.solution.user.domain.Name.InvalidNameException
import com.example.excepcionesexcepcionales.solution.user.domain.PhoneNumber.InvalidPhoneNumberException
import com.example.excepcionesexcepcionales.solution.user.domain.Surname.InvalidSurnameException
import com.example.excepcionesexcepcionales.solution.user.domain.User.UserAlreadyExistsException
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.CreateUserRequestBody
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.INVALID_EMAIL
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.INVALID_NAME
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.INVALID_PHONE_NUMBER
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.INVALID_SURNAME
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.USER_ALREADY_EXISTS
import java.time.ZonedDateTime
import java.util.UUID
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.CREATED
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestController
class ImperativeCreateUserController(private val handler: ImperativeCreateUserCommandHandler) {

    @PostMapping("/imperative/users")
    @ResponseStatus(CREATED)
    fun create(@RequestBody body: CreateUserRequestBody): Unit =
        with(body) {
            handler.handle(
                ImperativeCreateUserCommand(
                    id = UUID.randomUUID(),
                    email = email,
                    phonePrefix = phonePrefix,
                    phoneNumber = phoneNumber,
                    name = name,
                    surname = surname,
                    createdOn = ZonedDateTime.now()
                )
            )
        }

    @RestControllerAdvice(assignableTypes = [ImperativeCreateUserController::class])
    class ImperativeCreateUserControllerExceptionHandler {
        @ExceptionHandler(value = [UserAlreadyExistsException::class])
        @ResponseStatus(CONFLICT)
        fun userAlreadyExists(e: UserAlreadyExistsException) = USER_ALREADY_EXISTS

        @ExceptionHandler(value = [InvalidNameException::class])
        @ResponseStatus(BAD_REQUEST)
        fun invalidName(e: InvalidNameException) = INVALID_NAME

        @ExceptionHandler(value = [InvalidSurnameException::class])
        @ResponseStatus(BAD_REQUEST)
        fun handleNotFound(e: InvalidSurnameException) = INVALID_SURNAME

        @ExceptionHandler(value = [InvalidEmailException::class])
        @ResponseStatus(BAD_REQUEST)
        fun handleNotFound(e: InvalidEmailException) = INVALID_EMAIL

        @ExceptionHandler(value = [InvalidPhoneNumberException::class])
        @ResponseStatus(BAD_REQUEST)
        fun handleNotFound(e: InvalidPhoneNumberException) = INVALID_PHONE_NUMBER
    }
}
