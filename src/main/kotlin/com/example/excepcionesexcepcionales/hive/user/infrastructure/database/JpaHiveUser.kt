package com.example.excepcionesexcepcionales.hive.user.infrastructure.database

import com.example.excepcionesexcepcionales.hive.user.domain.Email
import com.example.excepcionesexcepcionales.hive.user.domain.Role
import com.example.excepcionesexcepcionales.hive.user.domain.User
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Entity
@Table
data class JpaHiveUser(
    @Id
    val email: String,
    val name: String,
    val role: String
) {

    fun toDomain(): User = User(name = name, email = Email(value = email), role = Role.valueOf(role))
}

internal fun User.toJpa(): JpaHiveUser = JpaHiveUser(name = name, email = email.value, role = role.name)

@Repository
interface JpaUserRepository : CrudRepository<JpaHiveUser, String> {
    fun findAllByName(name: String): List<JpaHiveUser>
}
