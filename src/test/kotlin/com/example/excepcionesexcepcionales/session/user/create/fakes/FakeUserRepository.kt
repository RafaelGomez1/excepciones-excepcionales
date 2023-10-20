package com.example.excepcionesexcepcionales.session.user.create.fakes

import arrow.core.Either
import arrow.core.Either.Companion.catch
import com.example.excepcionesexcepcionales.session.user.domain.Email
import com.example.excepcionesexcepcionales.session.user.domain.ExistsUserCriteria
import com.example.excepcionesexcepcionales.session.user.domain.ExistsUserCriteria.ByEmail
import com.example.excepcionesexcepcionales.session.user.domain.ExistsUserCriteria.ById
import com.example.excepcionesexcepcionales.session.user.domain.RepositoryResult
import com.example.excepcionesexcepcionales.session.user.domain.RepositoryResult.Success
import com.example.excepcionesexcepcionales.session.user.domain.RepositoryResult.Unknown
import com.example.excepcionesexcepcionales.session.user.domain.User
import com.example.excepcionesexcepcionales.session.user.domain.UserId
import com.example.excepcionesexcepcionales.session.user.domain.UserRepository
import com.example.excepcionesexcepcionales.session.user.secondaryadapter.database.toJpa
import com.example.excepcionesexcepcionales.solution.user.fakes.FakeRepository

object FakeUserRepository : UserRepository, FakeRepository<User> {
    override val elements = mutableListOf<User>()

    override fun findBy(userId: UserId): User = elements.first { it.id == userId }

    override fun existByUserId(userId: UserId): Boolean = elements.any { it.id == userId }

    override fun existByEmail(email: Email): Boolean = elements.any { it.email == email }

    override fun save(user: User) { elements.saveOrUpdate(user) }

    override fun exists(criteria: ExistsUserCriteria): RepositoryResult<Boolean> =
        runCatching {
            when(criteria) {
                is ByEmail -> elements.any { it.email == criteria.email }
                is ById -> elements.any { it.id == criteria.id }
            }
        }
            .map { result -> Success(result) }
            .getOrElse { error -> Unknown(error) }

    override fun saveSealed(user: User): RepositoryResult<Unit> =
        runCatching { elements.saveOrUpdate(user) }
            .map { Success(Unit) }
            .getOrElse { error -> Unknown(error) }

    override fun existsEither(criteria: ExistsUserCriteria): Either<Throwable, Boolean> = catch {
        when (criteria) {
            is ByEmail -> elements.any { it.email == criteria.email }
            is ById -> elements.any { it.id == criteria.id }
        }
    }

    override fun eitherSave(user: User): Either<Throwable, Unit> = catch { elements.saveOrUpdate(user) }
}
