package com.example.excepcionesexcepcionales.hive.user.application.create.fp

import arrow.core.Either
import arrow.core.raise.either
import com.example.excepcionesexcepcionales.hive.user.application.create.fp.CreateUserError.InvalidEmail
import com.example.excepcionesexcepcionales.hive.user.application.create.fp.CreateUserError.InvalidRole
import com.example.excepcionesexcepcionales.hive.user.application.create.initial.CreateUserData
import com.example.excepcionesexcepcionales.hive.user.domain.Email
import com.example.excepcionesexcepcionales.hive.user.domain.Role
import com.example.excepcionesexcepcionales.hive.user.domain.UserRepository
import org.springframework.stereotype.Component

@Component
class HiveCreateUserMediator(
    repository: UserRepository
) {

    private val createUser = HiveUserCreator(repository)

    operator fun invoke(data: CreateUserRequest): Either<CreateUserError, Unit> = either {
        val email = Email.createOrElse(data.email) { InvalidEmail }.bind()
        val role = Role.createOrElse(data.role) { InvalidRole }.bind()
        val creatorRole = Role.createOrElse(data.creator) { InvalidRole }.bind()

        createUser(
            email = email,
            role = role,
            name = data.name,
            creator = creatorRole
        ).bind()
    }
}

data class CreateUserRequest(
    val creator: String,
    val name: String,
    val email: String,
    val role: String
)
