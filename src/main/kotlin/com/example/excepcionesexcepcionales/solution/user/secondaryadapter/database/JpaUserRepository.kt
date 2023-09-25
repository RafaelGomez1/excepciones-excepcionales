package com.example.excepcionesexcepcionales.solution.user.secondaryadapter.database

import java.util.UUID
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaUserRepository : CrudRepository<JpaUser, UUID> {
    fun existsByEmailIgnoreCase(email: String): Boolean
}
