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
import com.example.excepcionesexcepcionales.solution.user.domain.SolutionUser
import com.example.excepcionesexcepcionales.solution.user.domain.UserId
import com.example.excepcionesexcepcionales.solution.user.domain.SolutionUserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class H2SolutionUserRepository(private val jpaRepository: JpaUserRepository) : SolutionUserRepository {
    override fun findBy(userId: UserId): SolutionUser {
        TODO("Not yet implemented")
    }

    override fun existBy(userId: UserId): Boolean {
        TODO("Not yet implemented")
    }

    override fun existBy(email: Email): Boolean = jpaRepository.existsByEmailIgnoreCase(email.value)

    override fun save(user: SolutionUser) { jpaRepository.save(user.toJpa()) }

    override fun findBySealed(userId: UserId): RepositoryResult<SolutionUser> {
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

    override fun saveSealed(user: SolutionUser): RepositoryResult<SolutionUser> =
        runCatching { jpaRepository.save(user.toJpa()) }
            .map { Success(user) }
            .getOrElse { error -> Unknown(error) }

    override fun find(criteria: FindUserCriteria): SolutionUser? =
        when(criteria) {
            is FindUserCriteria.ById -> jpaRepository.findByIdOrNull(criteria.id.value)
        }?.toSolutionDomain()

    override fun exists(criteria: ExistsUserCriteria): Boolean =
        when(criteria) {
            is ByEmail -> jpaRepository.existsByEmailIgnoreCase(criteria.email.value)
            is ById -> jpaRepository.existsById(criteria.id.value)
        }
}
