package com.example.excepcionesexcepcionales.solution.user.domain

import arrow.core.Either

interface UserRepository {
    fun findBy(userId: UserId): User
    fun existBy(userId: UserId): Boolean
    fun existBy(email: Email): Boolean
    fun save(user: User)

    // Sealed Version
    fun findBySealed(userId: UserId): RepositoryResult<User>
    fun existBySealed(email: Email): RepositoryResult<Boolean>
    fun existBySealed(userId: UserId): RepositoryResult<Boolean>
    fun saveSealed(user: User): RepositoryResult<User>

    // Functional Version
    fun find(criteria: FindUserCriteria): Either<Throwable, User>
    fun exists(criteria: ExistsUserCriteria): Either<Throwable, Boolean>
    fun eitherSave(user: User): Either<Throwable, Unit>
}

fun <Error> UserRepository.saveOrElse(
    user: User,
    onError: (Throwable) -> Error
): Either<Error, User> =
    eitherSave(user)
        .mapLeft{ error -> onError(error) }
        .map { user }

sealed interface RepositoryResult<T> {
    class Success<T>(val value: T): RepositoryResult<T>
    class Unknown<T>(val error: Throwable): RepositoryResult<T>
}

sealed interface ExistsUserCriteria {
    class ById(val id: UserId) : ExistsUserCriteria
    class ByEmail(val email: Email) : ExistsUserCriteria
}

sealed interface FindUserCriteria {
    class ById(val id: UserId) : FindUserCriteria
}
