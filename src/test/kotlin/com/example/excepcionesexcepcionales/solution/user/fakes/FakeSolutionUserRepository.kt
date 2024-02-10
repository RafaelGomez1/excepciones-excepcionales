package com.example.excepcionesexcepcionales.solution.user.fakes

import com.example.excepcionesexcepcionales.solution.user.domain.*
import com.example.excepcionesexcepcionales.solution.user.domain.ExistsUserCriteria.ByEmail
import com.example.excepcionesexcepcionales.solution.user.domain.ExistsUserCriteria.ById
import com.example.excepcionesexcepcionales.solution.user.domain.RepositoryResult.Success
import com.example.excepcionesexcepcionales.solution.user.domain.FindUserCriteria.ById as FindById

object FakeSolutionUserRepository : SolutionUserRepository, FakeRepository<SolutionUser> {
    override val elements = mutableListOf<SolutionUser>()

    override fun findBy(userId: UserId): SolutionUser = elements.first { it.id == userId }

    override fun existBy(userId: UserId): Boolean = elements.any { it.id == userId }

    override fun existBy(email: Email): Boolean = elements.any { it.email == email }

    override fun save(user: SolutionUser) { elements.saveOrUpdate(user) }

    override fun findBySealed(userId: UserId): RepositoryResult<SolutionUser> = Success(elements.first { it.id == userId })

    override fun existBySealed(email: Email): RepositoryResult<Boolean> = Success(elements.any { it.email == email })

    override fun existBySealed(userId: UserId): RepositoryResult<Boolean> = Success(elements.any { it.id == userId })

    override fun saveSealed(user: SolutionUser): RepositoryResult<SolutionUser> {
        elements.saveOrUpdate(user)
        return Success(user)
    }

    override fun find(criteria: FindUserCriteria): SolutionUser? =
        when(criteria) {
            is FindById -> elements.first { it.id == criteria.id }
        }

    override fun exists(criteria: ExistsUserCriteria): Boolean =
        when(criteria) {
            is ByEmail -> elements.any { it.email == criteria.email }
            is ById -> elements.any { it.id == criteria.id }
        }
}
