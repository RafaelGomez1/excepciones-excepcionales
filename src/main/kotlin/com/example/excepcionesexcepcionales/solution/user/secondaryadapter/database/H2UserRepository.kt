package com.example.excepcionesexcepcionales.solution.user.secondaryadapter.database

import arrow.core.Either
import arrow.core.Either.Companion.catch
import com.example.excepcionesexcepcionales.solution.user.domain.Email
import com.example.excepcionesexcepcionales.solution.user.domain.ExistsUserCriteria
import com.example.excepcionesexcepcionales.solution.user.domain.ExistsUserCriteria.ByEmail
import com.example.excepcionesexcepcionales.solution.user.domain.ExistsUserCriteria.ById
import com.example.excepcionesexcepcionales.solution.user.domain.FindUserCriteria
import com.example.excepcionesexcepcionales.solution.user.domain.RepositoryResult
import com.example.excepcionesexcepcionales.solution.user.domain.RepositoryResult.Success
import com.example.excepcionesexcepcionales.solution.user.domain.RepositoryResult.Unknown
import com.example.excepcionesexcepcionales.solution.user.domain.User
import com.example.excepcionesexcepcionales.solution.user.domain.UserId
import com.example.excepcionesexcepcionales.solution.user.domain.UserRepository
import org.springframework.stereotype.Component

@Component
class H2UserRepository(private val jpaRepository: JpaUserRepository) : UserRepository {
    override fun findBy(userId: UserId): User {
        TODO("Not yet implemented")
    }

    override fun existBy(userId: UserId): Boolean {
        TODO("Not yet implemented")
    }

    override fun existBy(email: Email): Boolean = jpaRepository.existsByEmailIgnoreCase(email.value)

    override fun save(user: User) { jpaRepository.save(user.toJpa()) }

    override fun findBySealed(userId: UserId): RepositoryResult<User> {
        TODO("Not yet implemented")
    }

    override fun existBySealed(email: Email): RepositoryResult<Boolean> =
        runCatching { jpaRepository.existsByEmailIgnoreCase(email.value) }
            .map { result -> Success(result) }
            .getOrElse { error -> Unknown(error) }

    override fun existBySealed(userId: UserId): RepositoryResult<Boolean> =
        runCatching { jpaRepository.existsById(userId.value) }
            .map { result -> Success(result) }
            .getOrElse { error -> Unknown(error) }

    override fun saveSealed(user: User): RepositoryResult<User> =
        runCatching { jpaRepository.save(user.toJpa()) }
            .map { Success(user) }
            .getOrElse { error -> Unknown(error) }

    override fun find(criteria: FindUserCriteria): Either<Throwable, User> {
        TODO("Not yet implemented")
    }

    override fun exists(criteria: ExistsUserCriteria): Either<Throwable, Boolean> = catch {
        when(criteria) {
            is ByEmail -> jpaRepository.existsByEmailIgnoreCase(criteria.email.value)
            is ById -> jpaRepository.existsById(criteria.id.value)
        }
    }

    override fun eitherSave(user: User): Either<Throwable, Unit> = catch { jpaRepository.save(user.toJpa()) }
}
