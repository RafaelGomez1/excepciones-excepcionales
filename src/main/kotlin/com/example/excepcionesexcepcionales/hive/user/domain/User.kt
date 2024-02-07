package com.example.excepcionesexcepcionales.hive.user.domain

import arrow.core.Either
import arrow.core.left
import arrow.core.right

data class User(
    val name: String,
    val email: Email,
    val role: Role
)

enum class Role {
    SUPERADMIN, MANAGER, ADMIN, BASIC;

    companion object {
        fun <Error> createOrElse(role: String, onError: () -> Error): Either<Error, Role> {
            val matchedRole = values().find { it.name.equals(role, ignoreCase = true) }
            return matchedRole?.right() ?: onError().left()
        }
    }
}

data class Email(val value: String) {
    init {
        if (!value.matches(emailRegex)) {
            throw UserInvalidEmailException(value)
        }
    }

    companion object {
        private val emailRegex = Regex("^[\\w\\-.+]+@([\\w-]+\\.)+[\\w-]{2,4}\$")

        fun <Error> createOrElse(value: String, onError: () -> Error): Either<Error, Email> =
            if(emailRegex.matches(value)) Email(value).right()
            else onError().left()
    }
}
