package com.example.excepcionesexcepcionales.solution.user.fakes

import arrow.core.Either
import arrow.core.Either.Companion.catch
import com.example.excepcionesexcepcionales.solution.user.domain.Email
import com.example.excepcionesexcepcionales.solution.user.domain.ExistsUserCriteria
import com.example.excepcionesexcepcionales.solution.user.domain.ExistsUserCriteria.ByEmail
import com.example.excepcionesexcepcionales.solution.user.domain.ExistsUserCriteria.ById
import com.example.excepcionesexcepcionales.solution.user.domain.FindUserCriteria
import com.example.excepcionesexcepcionales.solution.user.domain.RepositoryResult
import com.example.excepcionesexcepcionales.solution.user.domain.RepositoryResult.Success
import com.example.excepcionesexcepcionales.solution.user.domain.User
import com.example.excepcionesexcepcionales.solution.user.domain.UserId
import com.example.excepcionesexcepcionales.solution.user.domain.UserRepository

object FakeUserRepository : UserRepository, FakeRepository<User> {
    override val elements = mutableListOf<User>()

    override fun findBy(userId: UserId): User = elements.first { it.id == userId }

    override fun existBy(userId: UserId): Boolean = elements.any { it.id == userId }

    override fun existBy(email: Email): Boolean = elements.any { it.email == email }

    override fun save(user: User) { elements.saveOrUpdate(user) }

    override fun findBySealed(userId: UserId): RepositoryResult<User> = Success(elements.first { it.id == userId })

    override fun existBySealed(email: Email): RepositoryResult<Boolean> = Success(elements.any { it.email == email })

    override fun existBySealed(userId: UserId): RepositoryResult<Boolean> = Success(elements.any { it.id == userId })

    override fun saveSealed(user: User): RepositoryResult<User> {
        elements.saveOrUpdate(user)
        return Success(user)
    }

    override fun find(criteria: FindUserCriteria): Either<Throwable, User> = catch {
        when(criteria) {
            is FindUserCriteria.ById -> elements.first { it.id == criteria.id }
        }
    }

    override fun exists(criteria: ExistsUserCriteria): Either<Throwable, Boolean> = catch {
        when(criteria) {
            is ByEmail -> elements.any { it.email == criteria.email }
            is ById -> elements.any { it.id == criteria.id }
        }
    }

    override fun eitherSave(user: User): Either<Throwable, Unit> = catch {
        elements.saveOrUpdate(user)
    }
}
