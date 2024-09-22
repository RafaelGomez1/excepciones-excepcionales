package com.example.excepcionesexcepcionales.solution.user.create.imperative

import com.example.excepcionesexcepcionales.session.user.create.CreateUserTest
import com.example.excepcionesexcepcionales.session.user.create.CreateUserTest.Companion
import com.example.excepcionesexcepcionales.solution.user.application.create.imperative.ImperativeCreateUserCommandHandler
import com.example.excepcionesexcepcionales.solution.user.domain.SolutionUser.UserAlreadyExistsException
import com.example.excepcionesexcepcionales.solution.user.fakes.FakeDomainEventPublisher
import com.example.excepcionesexcepcionales.solution.user.fakes.FakeSolutionUserRepository
import com.example.excepcionesexcepcionales.solution.user.mothers.ImperativeCreateUserCommandMother
import com.example.excepcionesexcepcionales.solution.user.mothers.UserCreatedEventMother
import com.example.excepcionesexcepcionales.solution.user.mothers.UserMother
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ImperativeCreateUserTest {

    private val repository = FakeSolutionUserRepository
    private val publisher = FakeDomainEventPublisher
    private val handler = ImperativeCreateUserCommandHandler(repository, publisher)

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
        repository.assertContains(user)
        publisher.assertPublished(expectedEvent)
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
        publisher.assertNoEventsPublished()
    }

    private fun `user already exists`() {
        repository.save(user)
    }

    private val user = UserMother.incomplete()
    private val command = ImperativeCreateUserCommandMother.fromUser(user)
    private val expectedEvent = UserCreatedEventMother.fromUser(user)
}
