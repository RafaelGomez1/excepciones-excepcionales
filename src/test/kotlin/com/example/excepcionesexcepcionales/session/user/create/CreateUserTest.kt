package com.example.excepcionesexcepcionales.session.user.create

import com.example.excepcionesexcepcionales.session.user.application.create.CreateUserCommandHandler
import com.example.excepcionesexcepcionales.solution.user.domain.User.UserAlreadyExistsException
import com.example.excepcionesexcepcionales.solution.user.fakes.FakeDomainEventPublisher
import com.example.excepcionesexcepcionales.solution.user.fakes.FakeUserRepository
import com.example.excepcionesexcepcionales.solution.user.mothers.CreateUserCommandMother
import com.example.excepcionesexcepcionales.solution.user.mothers.UserCreatedEventMother
import com.example.excepcionesexcepcionales.solution.user.mothers.UserMother
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CreateUserTest {

    private val repository = FakeUserRepository
    private val publisher = FakeDomainEventPublisher
    private val handler = CreateUserCommandHandler(repository, publisher)

    @BeforeEach
    fun setUp() {
        repository.resetFake()
        publisher.resetFake()
    }

    @Test
    fun `should create user and publish event`() {
        // When
        handler.handle(command)

        // Then
        assertTrue { repository.wasPersisted(user) }
        assertTrue { publisher.wasPublished(expectedEvent) }
    }

    @Test
    fun `should throw exception if user already exists`() {
        // Given
        `user already exists`()

        // When
        assertThrows<UserAlreadyExistsException> {
            handler.handle(command)
        }

        // Then
        assertFalse { publisher.wasPublished(expectedEvent) }
    }

    private fun `user already exists`() {
        repository.save(user)
    }

    private val user = UserMother.incomplete()
    private val command = CreateUserCommandMother.fromUser(user)
    private val expectedEvent = UserCreatedEventMother.fromUser(user)
}
