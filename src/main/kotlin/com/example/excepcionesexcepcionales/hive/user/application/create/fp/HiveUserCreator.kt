package com.example.excepcionesexcepcionales.hive.user.application.create.fp

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.right
import com.example.excepcionesexcepcionales.hive.user.application.create.fp.CreateUserError.UserAlreadyExists
import com.example.excepcionesexcepcionales.hive.user.application.create.fp.CreateUserError.UserRoleNotAllowed
import com.example.excepcionesexcepcionales.hive.user.domain.Email
import com.example.excepcionesexcepcionales.hive.user.domain.ExistUserCriteria.ByEmail
import com.example.excepcionesexcepcionales.hive.user.domain.Role
import com.example.excepcionesexcepcionales.hive.user.domain.Role.*
import com.example.excepcionesexcepcionales.hive.user.domain.User
import com.example.excepcionesexcepcionales.hive.user.domain.UserRepository

class HiveUserCreator(private val repository: UserRepository) {

    operator fun invoke(email: Email, role: Role, name: String, creator: Role): Either<CreateUserError, Unit> = either {
        guardUserAlreadyExists(email).bind()
        guardRolesAreAllowed(creator, role).bind()

        val user = User(name = name, email = email, role = role)

        repository.save(user)
    }

    private fun guardUserAlreadyExists(email: Email): Either<CreateUserError, Unit> =
        if (repository.exist(ByEmail(email))) UserAlreadyExists.left()
        else Unit.right()

    private fun guardRolesAreAllowed(creator: Role, role: Role): Either<CreateUserError, Unit> =
        when (creator) {
            SUPERADMIN, ADMIN -> listOf(ADMIN, MANAGER, BASIC)
            MANAGER -> listOf(MANAGER, BASIC)
            BASIC -> emptyList()
        }.let {
            if (!it.contains(role)) UserRoleNotAllowed.left()
            else Unit.right()
        }
}

sealed interface CreateUserError {
    object InvalidEmail : CreateUserError
    object InvalidRole : CreateUserError

    object UserAlreadyExists : CreateUserError
    object UserRoleNotAllowed : CreateUserError
}
