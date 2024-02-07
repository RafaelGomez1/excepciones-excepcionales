package com.example.excepcionesexcepcionales.session.user.application.create

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.raise.either
import com.example.excepcionesexcepcionales.session.user.application.create.CreateUserError.Unknown
import com.example.excepcionesexcepcionales.session.user.application.create.CreateUserError.UserAlreadyExists
import com.example.excepcionesexcepcionales.session.user.domain.Email
import com.example.excepcionesexcepcionales.session.user.domain.ExistsUserCriteria.ByEmail
import com.example.excepcionesexcepcionales.session.user.domain.Name
import com.example.excepcionesexcepcionales.session.user.domain.PhoneNumber
import com.example.excepcionesexcepcionales.session.user.domain.Surname
import com.example.excepcionesexcepcionales.session.user.domain.User
import com.example.excepcionesexcepcionales.session.user.domain.UserId
import com.example.excepcionesexcepcionales.session.user.domain.UserRepository
import com.example.excepcionesexcepcionales.session.user.domain.existsOrElse
import com.example.excepcionesexcepcionales.session.user.domain.saveOrElse
import com.example.excepcionesexcepcionales.shared.error.failIfTrue
import com.example.excepcionesexcepcionales.shared.event.DomainEventPublisher
import com.example.excepcionesexcepcionales.shared.event.publishOrElse
import java.time.ZonedDateTime

class UserCreator(
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
    ): Either<CreateUserError, Unit> = either {
        guardUserDoesNotExists(email).bind()

        val user = User.create(id, email, phoneNumber, createdOn, name, surname)

        repository.saveOrElse(user) { Unknown(it) }.bind()
        publisher.publishOrElse(user.pullEvents()) { Unknown(it) }.bind()
    }

    private fun save(user: User) = repository.saveOrElse(user) { Unknown(it) }

    private fun publish(user: User) = publisher.publishOrElse(user.pullEvents()) { Unknown(it) }

    private fun guardUserDoesNotExists(email: Email) =
        repository.existsOrElse(ByEmail(email)) { Unknown(it) }
            .failIfTrue { UserAlreadyExists }

}

sealed interface CreateUserError2 {
    object InvalidName: CreateUserError2
    object InvalidSurname: CreateUserError2
    object InvalidEmail: CreateUserError2
    object InvalidPhoneNumber: CreateUserError2
    object UserAlreadyExists: CreateUserError2
    class Unknown(val reason: Throwable): CreateUserError2
}

sealed class CreateUserError {
    object InvalidName: CreateUserError()
    object InvalidSurname: CreateUserError()
    object InvalidEmail: CreateUserError()
    object InvalidPhoneNumber: CreateUserError()
    object UserAlreadyExists: CreateUserError()
    class Unknown(val reason: Throwable): CreateUserError()
}

//private fun RepositoryResult<Boolean>.createUser(
//    id: UserId,
//    email: Email,
//    phoneNumber: PhoneNumber,
//    createdOn: ZonedDateTime,
//    name: Name,
//    surname: Surname,
//): CreateUserResult =
//    when(this) {
//        is RepositoryResult.RepoSuccess ->
//            if (value) UserAlreadyExists
//            else Success(User.create(id, email, phoneNumber, createdOn, name, surname))
//        is RepositoryResult.RepoUnknown -> Unknown(error)
//    }
//
//private fun CreateUserResult.save() =
//    if (this is Success)
//        when(val result = repository.saveSealed(user)) {
//            is RepositoryResult.RepoSuccess -> Success(user)
//            is RepositoryResult.RepoUnknown -> Unknown(result.error)
//        }
//    else this
//
//private fun CreateUserResult.publish() =
//    if (this is Success)
//        when(val result = publisher.safePublish(user.pullEvents())) {
//            is PublisherResult.PubSuccess -> Success(user)
//            is PublisherResult.PubUnknown -> Unknown(result.error)
//        }
//    else this
