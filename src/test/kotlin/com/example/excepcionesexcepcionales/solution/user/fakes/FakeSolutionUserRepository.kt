package com.example.excepcionesexcepcionales.solution.user.fakes

import com.example.excepcionesexcepcionales.solution.user.domain.*
import com.example.excepcionesexcepcionales.solution.user.domain.ExistsUserCriteria.ByEmail
import com.example.excepcionesexcepcionales.solution.user.domain.ExistsUserCriteria.ById
import com.example.excepcionesexcepcionales.solution.user.domain.RepositoryResult.Success
import com.example.excepcionesexcepcionales.solution.user.domain.FindUserCriteria.ById as FindById

object FakeSolutionUserRepository : SolutionUserRepository, FakeRepositoryV2<UserId, SolutionUser> {
    override val elements = mutableMapOf<UserId, SolutionUser>()
    override val errors = mutableListOf<Throwable>()

    override fun findBy(userId: UserId): SolutionUser =
        elements.getValue(userId)

    override fun existBy(userId: UserId): Boolean = elements.contains(userId)
    override fun existBy(email: Email): Boolean = elements.values.any { it.email == email }

    override fun save(user: SolutionUser) { elements.saveOrUpdate(user, user.id) }

    override fun findBySealed(userId: UserId): RepositoryResult<SolutionUser> = Success(elements.getValue(userId))

    override fun existBySealed(email: Email): RepositoryResult<Boolean> = Success(elements.values.any { it.email == email })

    override fun existBySealed(userId: UserId): RepositoryResult<Boolean> = Success(elements.contains(userId))

    override fun saveSealed(user: SolutionUser): RepositoryResult<SolutionUser> {
        elements.saveOrUpdate(user, user.id)
        return Success(user)
    }

    override fun find(criteria: FindUserCriteria): SolutionUser? =
        when(criteria) {
            is FindById -> elements[criteria.id]
        }

    override fun exists(criteria: ExistsUserCriteria): Boolean =
        when(criteria) {
            is ByEmail -> elements.values.any { it.email == criteria.email }
            is ById -> elements.contains(criteria.id)
        }
}
