package com.example.excepcionesexcepcionales.solution.user.domain

import arrow.core.Either

interface SolutionUserRepository {
    fun findBy(userId: UserId): SolutionUser
    fun existBy(userId: UserId): Boolean
    fun existBy(email: Email): Boolean
    fun save(user: SolutionUser)

    // Sealed Version
    fun findBySealed(userId: UserId): RepositoryResult<SolutionUser>
    fun existBySealed(email: Email): RepositoryResult<Boolean>
    fun existBySealed(userId: UserId): RepositoryResult<Boolean>
    fun saveSealed(user: SolutionUser): RepositoryResult<SolutionUser>

    // Functional Version
    fun find(criteria: FindUserCriteria): Either<Throwable, SolutionUser>
    fun exists(criteria: ExistsUserCriteria): Either<Throwable, Boolean>
    fun eitherSave(user: SolutionUser): Either<Throwable, Unit>
}

fun <Error> SolutionUserRepository.saveOrElse(
    user: SolutionUser,
    onError: (Throwable) -> Error
): Either<Error, SolutionUser> =
    eitherSave(user)
        .mapLeft{ error -> onError(error) }
        .map { user }

fun <Error> SolutionUserRepository.findOrElse(
    criteria: FindUserCriteria,
    onNotFound: () -> Error,
    onError: (Throwable) -> Error,
): Either<Error, SolutionUser> =
    find(criteria)
        .mapLeft{ error ->
            if (error is NoSuchElementException) onNotFound()
            else onError(error)
        }

fun <Error> SolutionUserRepository.existsOrElse(
    criteria: ExistsUserCriteria,
    onError: (Throwable) -> Error
): Either<Error, Boolean> =
    exists(criteria)
        .mapLeft{ error -> onError(error) }

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
