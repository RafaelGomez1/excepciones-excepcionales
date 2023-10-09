package com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.imperative

import com.example.excepcionesexcepcionales.shared.clock.Clock
import com.example.excepcionesexcepcionales.shared.id.IdGenerator
import com.example.excepcionesexcepcionales.solution.user.application.create.imperative.ImperativeCreateUserCommand
import com.example.excepcionesexcepcionales.solution.user.application.create.imperative.ImperativeCreateUserCommandHandler
import com.example.excepcionesexcepcionales.solution.user.domain.Email.InvalidEmailException
import com.example.excepcionesexcepcionales.solution.user.domain.Name.InvalidNameException
import com.example.excepcionesexcepcionales.solution.user.domain.PhoneNumber.InvalidPhoneNumberException
import com.example.excepcionesexcepcionales.solution.user.domain.Surname.InvalidSurnameException
import com.example.excepcionesexcepcionales.solution.user.domain.SolutionUser.UserAlreadyExistsException
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.CreateUserRequestBody
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.INVALID_EMAIL
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.INVALID_NAME
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.INVALID_PHONE_NUMBER
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.INVALID_SURNAME
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.USER_ALREADY_EXISTS
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
class ImperativeCreateUserController(
    private val handler: ImperativeCreateUserCommandHandler,
    private val idGenerator: IdGenerator,
    private val clock: Clock
) {

    @PostMapping("/imperative/users")
    @ResponseStatus(CREATED)
    fun create(@RequestBody body: CreateUserRequestBody): Unit =
        with(body) {
            handler.handle(
                ImperativeCreateUserCommand(
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
}

@RestControllerAdvice(assignableTypes = [ImperativeCreateUserController::class])
class ImperativeCreateUserControllerExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException::class)
    @ResponseStatus(CONFLICT)
    fun userAlreadyExists(e: UserAlreadyExistsException) = USER_ALREADY_EXISTS

    @ExceptionHandler(InvalidNameException::class)
    @ResponseStatus(BAD_REQUEST)
    fun invalidName(e: InvalidNameException) = INVALID_NAME

    @ExceptionHandler(InvalidSurnameException::class)
    @ResponseStatus(BAD_REQUEST)
    fun handleNotFound(e: InvalidSurnameException) = INVALID_SURNAME

    @ExceptionHandler(InvalidEmailException::class)
    @ResponseStatus(BAD_REQUEST)
    fun handleNotFound(e: InvalidEmailException) = INVALID_EMAIL

    @ExceptionHandler(InvalidPhoneNumberException::class)
    @ResponseStatus(BAD_REQUEST)
    fun handleNotFound(e: InvalidPhoneNumberException) = INVALID_PHONE_NUMBER
}
