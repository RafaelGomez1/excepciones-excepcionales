package com.example.excepcionesexcepcionales.session.user.domain

import arrow.core.Either

interface UserRepository {
    fun findBy(userId: UserId): User
    fun existByUserId(userId: UserId): Boolean
    fun existByEmail(email: Email): Boolean
    fun save(user: User)

    fun exists(criteria: ExistsUserCriteria): RepositoryResult<Boolean>
    fun saveSealed(user: User): RepositoryResult<Unit>
    
    fun existsEither(criteria: ExistsUserCriteria): Either<Throwable, Boolean>
    fun eitherSave(user: User): Either<Throwable, Unit>
}

sealed interface RepositoryResult<T> {
    class Success<T>(val value: T): RepositoryResult<T>
    class Unknown<T>(val error: Throwable): RepositoryResult<T>
}

sealed interface ExistsUserCriteria {
    class ById(val id: UserId) : ExistsUserCriteria
    class ByEmail(val email: Email) : ExistsUserCriteria
}

fun <Error> UserRepository.existsOrElse(
    criteria: ExistsUserCriteria,
    onError: (Throwable) -> Error,
): Either<Error, Boolean> =
    existsEither(criteria)
        .mapLeft{ error -> onError(error) }

fun <Error> UserRepository.saveOrElse(user: User, onError: (Throwable) -> Error): Either<Error, User> =
    eitherSave(user)
        .mapLeft{ error -> onError(error) }
        .map { user }

