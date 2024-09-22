package com.example.excepcionesexcepcionales.hive.user.create

import com.example.excepcionesexcepcionales.hive.user.application.create.fp.HiveCreateUserMediator
import com.example.excepcionesexcepcionales.hive.user.domain.Role.*
import com.example.excepcionesexcepcionales.hive.user.fakes.FakeUserRepository
import com.example.excepcionesexcepcionales.hive.user.infrastructure.controller.FunctionalHiveCreateUserController
import com.example.excepcionesexcepcionales.hive.user.infrastructure.controller.ServerErrors.INVALID_EMAIL
import com.example.excepcionesexcepcionales.hive.user.infrastructure.controller.ServerErrors.INVALID_ROLE
import com.example.excepcionesexcepcionales.hive.user.infrastructure.controller.ServerErrors.ROLE_NOT_ALLOWED
import com.example.excepcionesexcepcionales.hive.user.infrastructure.controller.ServerErrors.USER_ALREADY_EXISTS
import com.example.excepcionesexcepcionales.hive.user.mothers.UserMother
import com.example.excepcionesexcepcionales.hive.user.mothers.UserRequestMother
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED

class CreateUserTest {

    private val repository = FakeUserRepository
    private val mediator = HiveCreateUserMediator(repository)

    private val controller = FunctionalHiveCreateUserController(mediator)

    @BeforeEach
    fun setUp() {
        repository.resetFake()
    }

    @Test
    fun `should create a new user`() {
        // When
        val result = controller.execute(request, SUPERADMIN.name)

        // Then
        assertEquals(CREATED, result.statusCode)
        assertEquals("{}", result.body)

        repository.assertContains(user)
    }

    @Test
    fun `should return BAD_REQUEST when email is not valid`() {
        // Given
        val invalidEmailRequest = request.copy(email = "aaaa")

        // When
        val result = controller.execute(invalidEmailRequest, SUPERADMIN.name)

        // Then
        assertEquals(BAD_REQUEST, result.statusCode)
        assertEquals(INVALID_EMAIL, result.body)

        repository.assertDoesNotContain(user)
    }

    @Test
    fun `should return BAD_REQUEST when role is not valid`() {
        // Given
        val invalidEmailRequest = request.copy(role = "aaaa")

        // When
        val result = controller.execute(invalidEmailRequest, SUPERADMIN.name)

        // Then
        assertEquals(BAD_REQUEST, result.statusCode)
        assertEquals(INVALID_ROLE, result.body)

        repository.assertDoesNotContain(user)
    }

    @Test
    fun `should return BAD_REQUEST when user already exists`() {
        // Given
        repository.save(user)

        // When
        val result = controller.execute(request, SUPERADMIN.name)

        // Then
        assertEquals(BAD_REQUEST, result.statusCode)
        assertEquals(USER_ALREADY_EXISTS, result.body)
    }

    @Test
    fun `should return BAD_REQUEST when user role is not allowed`() {
        // When
        val result = controller.execute(request, BASIC.name)

        // Then
        assertEquals(BAD_REQUEST, result.statusCode)
        assertEquals(ROLE_NOT_ALLOWED, result.body)

        repository.assertDoesNotContain(user)
    }

    private val user = UserMother.random(role = MANAGER)
    private val request = UserRequestMother.fromUser(user)
}
