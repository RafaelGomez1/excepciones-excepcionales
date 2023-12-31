package com.example.excepcionesexcepcionales.solution.user.application.create.sealed

import com.example.excepcionesexcepcionales.shared.event.DomainEventPublisher
import com.example.excepcionesexcepcionales.shared.event.safePublish
import com.example.excepcionesexcepcionales.solution.user.application.create.sealed.CreateUserResult.Success
import com.example.excepcionesexcepcionales.solution.user.application.create.sealed.CreateUserResult.Unknown
import com.example.excepcionesexcepcionales.solution.user.application.create.sealed.CreateUserResult.UserAlreadyExists
import com.example.excepcionesexcepcionales.solution.user.domain.Email
import com.example.excepcionesexcepcionales.solution.user.domain.Name
import com.example.excepcionesexcepcionales.solution.user.domain.PhoneNumber
import com.example.excepcionesexcepcionales.solution.user.domain.RepositoryResult
import com.example.excepcionesexcepcionales.solution.user.domain.SolutionUser
import com.example.excepcionesexcepcionales.solution.user.domain.SolutionUserRepository
import com.example.excepcionesexcepcionales.solution.user.domain.Surname
import com.example.excepcionesexcepcionales.solution.user.domain.UserId
import java.time.ZonedDateTime
import com.example.excepcionesexcepcionales.shared.event.PublisherResult.PubSuccess as PubSuccess
import com.example.excepcionesexcepcionales.shared.event.PublisherResult.PubUnknown as PubUnknown
import com.example.excepcionesexcepcionales.solution.user.domain.RepositoryResult.Success as RepoSuccess
import com.example.excepcionesexcepcionales.solution.user.domain.RepositoryResult.Unknown as RepoUnknown

class SealedUserCreator(
    private val repository: SolutionUserRepository,
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

    private fun guardUserExists(email: Email) = repository.existBySealed(email)

    private fun RepositoryResult<Boolean>.createUser(
        id: UserId,
        email: Email,
        phoneNumber: PhoneNumber,
        createdOn: ZonedDateTime,
        name: Name,
        surname: Surname,
    ): CreateUserResult =
        when(this) {
            is RepoSuccess ->
                if (value) UserAlreadyExists
                else Success(SolutionUser.create(id, email, phoneNumber, createdOn, name, surname))
            is RepoUnknown -> Unknown(this.error)
        }

    private fun CreateUserResult.save(): CreateUserResult =
        if(this is Success)
            when(val result = repository.saveSealed(user)) {
                is RepoSuccess -> Success(result.value)
                is RepoUnknown -> Unknown(result.error)
            }
        else this

    private fun CreateUserResult.publishEvent(): CreateUserResult =
        if(this is Success)
            when(val result = publisher.safePublish(user.pullEvents())) {
                is PubSuccess -> Success(user)
                is PubUnknown -> Unknown(result.error)
            }
        else this
}

sealed interface CreateUserResult {
    // Validation Errors
    object InvalidEmail : CreateUserResult
    object InvalidName : CreateUserResult
    object InvalidSurname : CreateUserResult
    object InvalidMobilePhone : CreateUserResult

    object UserAlreadyExists : CreateUserResult
    class Success(val user: SolutionUser): CreateUserResult
    class Unknown(val error: Throwable): CreateUserResult
}



