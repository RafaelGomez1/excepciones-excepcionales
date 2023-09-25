package com.example.excepcionesexcepcionales.solution.user.application.create.sealed

import com.example.excepcionesexcepcionales.shared.event.DomainEventPublisher
import com.example.excepcionesexcepcionales.solution.user.application.create.sealed.CreateUserResult.InvalidEmail
import com.example.excepcionesexcepcionales.solution.user.application.create.sealed.CreateUserResult.InvalidMobilePhone
import com.example.excepcionesexcepcionales.solution.user.application.create.sealed.CreateUserResult.InvalidName
import com.example.excepcionesexcepcionales.solution.user.application.create.sealed.CreateUserResult.InvalidSurname
import com.example.excepcionesexcepcionales.solution.user.application.create.sealed.CreateUserResult.Success
import com.example.excepcionesexcepcionales.solution.user.application.create.sealed.CreateUserResult.Unknown
import com.example.excepcionesexcepcionales.solution.user.application.create.sealed.CreateUserResult.UserAlreadyExists
import com.example.excepcionesexcepcionales.solution.user.domain.Email
import com.example.excepcionesexcepcionales.solution.user.domain.PhoneNumber
import com.example.excepcionesexcepcionales.solution.user.domain.Name
import com.example.excepcionesexcepcionales.solution.user.domain.RepositoryResult
import com.example.excepcionesexcepcionales.solution.user.domain.Surname
import com.example.excepcionesexcepcionales.solution.user.domain.User
import com.example.excepcionesexcepcionales.solution.user.domain.UserId
import com.example.excepcionesexcepcionales.solution.user.domain.UserRepository
import java.time.ZonedDateTime

class SealedUserCreator(
    private val repository: UserRepository,
    private val publisher: DomainEventPublisher
) {

    fun invoke(
        id: UserId,
        email: Email,
        phoneNumber: PhoneNumber,
        createdOn: ZonedDateTime,
        name: Name,
        surname: Surname
    ): CreateUserResult =
        guardUserExists(email)
            .createUser(id, email, phoneNumber, createdOn, name, surname)
            .save()
            .publishEvent()

    private fun guardUserExists(email: Email) =
        repository.existBySealed(email)

    private fun RepositoryResult<Boolean>.createUser(
        id: UserId,
        email: Email,
        phoneNumber: PhoneNumber,
        createdOn: ZonedDateTime,
        name: Name,
        surname: Surname,
    ): CreateUserResult =
        when(this) {
            is RepositoryResult.Success ->
                if (value) Success(User.create(id, email, phoneNumber, createdOn, name, surname))
                else UserAlreadyExists
            is RepositoryResult.Unknown -> Unknown(this.error)
        }

    private fun CreateUserResult.save(): CreateUserResult =
        when(this) {
            is Success -> {
                when(val result = repository.saveSealed(user)) {
                    is RepositoryResult.Success -> Success(result.value)
                    is RepositoryResult.Unknown -> Unknown(result.error)
                }
            }
            is Unknown -> this
            is UserAlreadyExists -> this
            InvalidEmail -> this
            InvalidMobilePhone -> this
            InvalidName -> this
            InvalidSurname -> this
        }

    private fun CreateUserResult.publishEvent(): CreateUserResult =
        runCatching { if(this is Success) publisher.publish(user.pullEvents()) }
            .map {
                when(this) {
                    is Success -> Success(user)
                    is Unknown -> this
                    is UserAlreadyExists -> this
                    InvalidEmail -> this
                    InvalidMobilePhone -> this
                    InvalidName -> this
                    InvalidSurname -> this
                }
            }.getOrElse { error -> Unknown(error) }
}

sealed interface CreateUserResult {
    // Validation Errors
    object InvalidEmail : CreateUserResult
    object InvalidName : CreateUserResult
    object InvalidSurname : CreateUserResult
    object InvalidMobilePhone : CreateUserResult

    object UserAlreadyExists : CreateUserResult
    class Success(val user: User): CreateUserResult
    class Unknown(val error: Throwable): CreateUserResult
}



