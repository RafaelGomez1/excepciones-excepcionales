package com.example.excepcionesexcepcionales.session.user.primaryAdapter.create

import com.example.excepcionesexcepcionales.session.user.application.create.CreateUserCommand
import com.example.excepcionesexcepcionales.session.user.application.create.CreateUserCommandHandler
import com.example.excepcionesexcepcionales.shared.clock.Clock
import com.example.excepcionesexcepcionales.shared.id.IdGenerator
import com.example.excepcionesexcepcionales.solution.user.domain.Email.InvalidEmailException
import com.example.excepcionesexcepcionales.solution.user.domain.Name.InvalidNameException
import com.example.excepcionesexcepcionales.solution.user.domain.PhoneNumber.InvalidPhoneNumberException
import com.example.excepcionesexcepcionales.solution.user.domain.Surname.InvalidSurnameException
import com.example.excepcionesexcepcionales.solution.user.domain.User.UserAlreadyExistsException
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.CreateUserRequestBody
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
class CreateUserController(
    private val handler: CreateUserCommandHandler,
    private val idGenerator: IdGenerator,
    private val clock: Clock
) {

    @PostMapping("/users")
    @ResponseStatus(CREATED)
    fun create(@RequestBody body: CreateUserRequestBody): Unit =
        with(body) {
            handler.handle(
                CreateUserCommand(
                    id = idGenerator.generate(),
                    email = email,
                    phonePrefix = phonePrefix,
                    phoneNumber = phoneNumber,
                    name = name,
                    surname = surname,
                    createdOn = clock.now()
                )
            )
        }

    @RestControllerAdvice(assignableTypes = [CreateUserController::class])
    class CreateUserControllerExceptionHandler {
        @ExceptionHandler(value = [UserAlreadyExistsException::class])
        @ResponseStatus(CONFLICT)
        fun userAlreadyExists(e: UserAlreadyExistsException) = "User already exists"

        @ExceptionHandler(value = [InvalidNameException::class])
        @ResponseStatus(BAD_REQUEST)
        fun invalidName(e: InvalidNameException) = "Name is blank or over 50 characters"

        @ExceptionHandler(value = [InvalidSurnameException::class])
        @ResponseStatus(BAD_REQUEST)
        fun handleNotFound(e: InvalidSurnameException) = "Surname is blank or over 80 characters"

        @ExceptionHandler(value = [InvalidEmailException::class])
        @ResponseStatus(BAD_REQUEST)
        fun handleNotFound(e: InvalidEmailException) = "Provided email is not valid"

        @ExceptionHandler(value = [InvalidPhoneNumberException::class])
        @ResponseStatus(BAD_REQUEST)
        fun handleNotFound(e: InvalidPhoneNumberException) = "Phone Number is not valid"
    }
}
