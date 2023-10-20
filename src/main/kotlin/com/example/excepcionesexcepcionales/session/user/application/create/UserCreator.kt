package com.example.excepcionesexcepcionales.session.user.application.create

import com.example.excepcionesexcepcionales.session.user.domain.Email
import com.example.excepcionesexcepcionales.session.user.domain.Name
import com.example.excepcionesexcepcionales.session.user.domain.PhoneNumber
import com.example.excepcionesexcepcionales.session.user.domain.Surname
import com.example.excepcionesexcepcionales.session.user.domain.User
import com.example.excepcionesexcepcionales.session.user.domain.User.UserAlreadyExistsException
import com.example.excepcionesexcepcionales.session.user.domain.UserId
import com.example.excepcionesexcepcionales.session.user.domain.UserRepository
import com.example.excepcionesexcepcionales.shared.event.DomainEventPublisher
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
    ) {
        guardUserExists(email)

        val user = User.create(id, email, phoneNumber, createdOn, name, surname)

        repository.save(user)
        publisher.publish(user.pullEvents())
    }

    private fun guardUserExists(email: Email) {
        if (repository.existByEmail(email)) throw UserAlreadyExistsException()
    }
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
//        is RepositoryResult.Success ->
//            if (value) UserAlreadyExist
//            else Success(User.create(id, email, phoneNumber, createdOn, name, surname))
//        is RepositoryResult.Unknown -> Unknown(error)
//    }
//
//private fun CreateUserResult.save() =
//    if (this is Success)
//        when(val result = repository.saveSealed(user)) {
//            is RepositoryResult.Success -> Success(user)
//            is RepositoryResult.Unknown -> Unknown(result.error)
//        }
//    else this
//
//private fun CreateUserResult.publish() =
//    if (this is Success)
//        when(val result = publisher.safePublish(user.pullEvents())) {
//            is PublisherResult.Success -> Success(user)
//            is PublisherResult.Unknown -> Unknown(result.error)
//        }
//    else this
