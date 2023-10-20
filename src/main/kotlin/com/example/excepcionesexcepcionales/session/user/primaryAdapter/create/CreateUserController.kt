package com.example.excepcionesexcepcionales.session.user.primaryAdapter.create

import com.example.excepcionesexcepcionales.session.user.application.create.CreateUserCommand
import com.example.excepcionesexcepcionales.session.user.application.create.CreateUserCommandHandler
import com.example.excepcionesexcepcionales.session.user.application.create.CreateUserResult
import com.example.excepcionesexcepcionales.session.user.application.create.CreateUserResult.InvalidEmail
import com.example.excepcionesexcepcionales.session.user.application.create.CreateUserResult.InvalidName
import com.example.excepcionesexcepcionales.session.user.application.create.CreateUserResult.InvalidPhoneNumber
import com.example.excepcionesexcepcionales.session.user.application.create.CreateUserResult.InvalidSurname
import com.example.excepcionesexcepcionales.session.user.application.create.CreateUserResult.Success
import com.example.excepcionesexcepcionales.session.user.application.create.CreateUserResult.Unknown
import com.example.excepcionesexcepcionales.session.user.application.create.CreateUserResult.UserAlreadyExists
import com.example.excepcionesexcepcionales.shared.clock.Clock
import com.example.excepcionesexcepcionales.shared.error.Response
import com.example.excepcionesexcepcionales.shared.error.withoutBody
import com.example.excepcionesexcepcionales.shared.id.IdGenerator
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

//    private fun CreateUserResult.toServerError(): Response<*> =
//        when(this) {
//            InvalidEmail -> Response.badRequest().body(INVALID_EMAIL)
//            InvalidName -> Response.badRequest().body(INVALID_NAME)
//            InvalidPhoneNumber -> Response.badRequest().body(INVALID_PHONE_NUMBER)
//            InvalidSurname -> Response.badRequest().body(INVALID_SURNAME)
//            UserAlreadyExists -> Response.status(CONFLICT).body(INVALID_SURNAME)
//            is Success -> Response.status(CREATED).withoutBody()
//            is Unknown -> throw reason
//        }
