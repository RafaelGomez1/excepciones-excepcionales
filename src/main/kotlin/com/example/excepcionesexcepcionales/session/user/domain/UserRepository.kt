package com.example.excepcionesexcepcionales.session.user.domain

import arrow.core.Either
import arrow.core.left
import arrow.core.right

interface UserRepository {
    fun findBy(userId: UserId): User
    fun existByUserId(userId: UserId): Boolean
    fun existByEmail(email: Email): Boolean
    fun save(user: User)

    fun find(criteria: FindUserCriteria): User?

    fun existsSealed(criteria: ExistsUserCriteria): RepositoryResult<Boolean>
    fun saveSealed(user: User): RepositoryResult<Unit>
    
    fun exists(criteria: ExistsUserCriteria): Boolean
}

sealed interface RepositoryResult<T> {
    class RepoSuccess<T>(val value: T): RepositoryResult<T>
    class RepoUnknown<T>(val error: Throwable): RepositoryResult<T>
}

sealed interface ExistsUserCriteria {
    class ById(val id: UserId) : ExistsUserCriteria
    class ByEmail(val email: Email) : ExistsUserCriteria
}

sealed interface FindUserCriteria {
    class ById(val id: UserId) : FindUserCriteria
}

fun <Error> UserRepository.findOrElse(
    criteria: FindUserCriteria,
    onError: () -> Error,
): Either<Error, User> =
    find(criteria)?.right() ?: onError().left()

