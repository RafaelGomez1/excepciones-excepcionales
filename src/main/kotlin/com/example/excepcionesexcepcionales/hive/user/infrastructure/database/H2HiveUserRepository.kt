package com.example.excepcionesexcepcionales.hive.user.infrastructure.database

import com.example.excepcionesexcepcionales.hive.user.domain.*
import com.example.excepcionesexcepcionales.hive.user.domain.FindUserCriteria.ByEmail
import com.example.excepcionesexcepcionales.hive.user.domain.SearchUserCriteria.All
import com.example.excepcionesexcepcionales.hive.user.domain.SearchUserCriteria.ByUsername
import com.example.excepcionesexcepcionales.hive.user.domain.ExistUserCriteria.ByEmail as ExistByEmail
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class H2HiveUserRepository(private val repository: JpaUserRepository) : UserRepository {
    override fun findByEmail(email: String): User? = repository.findByIdOrNull(email)?.toDomain()

    override fun search(criteria: SearchUserCriteria): List<User> =
        when(criteria) {
            All -> repository.findAll()
            is ByUsername -> repository.findAllByName(criteria.username)
        }.map { it.toDomain() }

    override fun find(criteria: FindUserCriteria): User? =
        when(criteria) {
            is ByEmail -> repository.findByIdOrNull(criteria.email.value)
        }?.toDomain()

    override fun exist(criteria: ExistUserCriteria): Boolean =
        when(criteria) {
            is ExistByEmail -> repository.existsById(criteria.email.value)
        }

    override fun save(user: User) { repository.save(user.toJpa()) }
    override fun delete(user: User) { repository.delete(user.toJpa()) }
}
