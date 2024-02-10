package com.example.excepcionesexcepcionales.solution.user.domain

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
    fun find(criteria: FindUserCriteria): SolutionUser?
    fun exists(criteria: ExistsUserCriteria): Boolean
}

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
