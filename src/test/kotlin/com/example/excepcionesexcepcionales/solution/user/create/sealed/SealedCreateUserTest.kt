package com.example.excepcionesexcepcionales.solution.user.create.sealed

import com.example.excepcionesexcepcionales.solution.user.application.create.sealed.SealedCreateUserCommandHandler
import com.example.excepcionesexcepcionales.solution.user.fakes.FakeClock
import com.example.excepcionesexcepcionales.solution.user.fakes.FakeDomainEventPublisher
import com.example.excepcionesexcepcionales.solution.user.fakes.FakeIdGenerator
import com.example.excepcionesexcepcionales.solution.user.fakes.FakeSolutionUserRepository
import com.example.excepcionesexcepcionales.solution.user.mothers.CreateUserRequestBodyMother
import com.example.excepcionesexcepcionales.solution.user.mothers.UserCreatedEventMother
import com.example.excepcionesexcepcionales.solution.user.mothers.UserMother
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.CreateUserRequestBody
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.INVALID_EMAIL
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.INVALID_NAME
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.INVALID_PHONE_NUMBER
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.INVALID_SURNAME
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.USER_ALREADY_EXISTS
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.sealed.SealedCreateUserController
import java.util.stream.Stream
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.CREATED

class SealedCreateUserTest {

    private val repository = FakeSolutionUserRepository
    private val publisher = FakeDomainEventPublisher
    private val idGenerator = FakeIdGenerator
    private val clock = FakeClock

    private val handler = SealedCreateUserCommandHandler(repository, publisher)

    private val controller = SealedCreateUserController(handler, idGenerator, clock)

    @BeforeEach
    fun setUp() {
        repository.resetFake()
        publisher.resetFake()
        idGenerator.resetFake()
        clock.resetFake()
    }

    @Test
    fun `should create user and publish event`() {
        // Given
        `ids will be generated`()
        `clock works`()

        // When
        val result = controller.create(body)

        // Then
        assertEquals(CREATED, result.statusCode)
        assertTrue { repository.contains(user) }
        assertTrue { publisher.wasPublished(expectedEvent) }
    }

    @Test
    fun `should return conflict if user already exists`() {
        // Given
        `ids will be generated`()
        `clock works`()
        `user already exists`()

        // When
        val result = controller.create(body)

        // Then
        assertEquals(CONFLICT, result.statusCode)
        assertEquals(USER_ALREADY_EXISTS, result.body)
        assertFalse { publisher.wasPublished(expectedEvent) }
    }


    @ParameterizedTest
    @MethodSource("validationErrors")
    fun `should return bad request if email is invalid`(body: CreateUserRequestBody, status: HttpStatus, error: String) {
        // Given
        `ids will be generated`()
        `clock works`()

        // When
        val result = controller.create(body)

        // Then
        assertEquals(status, result.statusCode)
        assertEquals(error, result.body)
    }

    private fun `user already exists`() {
        repository.save(user)
    }

    private fun `clock works`() {
        clock.shouldGenerate(user.createdOn)
    }

    private fun `ids will be generated`() {
        idGenerator.shouldGenerate(user.id.value)
    }

    companion object {
        private val user = UserMother.incomplete()
        private val body = CreateUserRequestBodyMother.fromUser(user)
        private val expectedEvent = UserCreatedEventMother.fromUser(user)

        @JvmStatic
        fun validationErrors() = Stream.of(
            Arguments.of(
                body.copy(email = "abe"),
                BAD_REQUEST,
                INVALID_EMAIL
            ),
            Arguments.of(
                body.copy(phoneNumber = "+3a1"),
                BAD_REQUEST,
                INVALID_PHONE_NUMBER
            ),
            Arguments.of(
                body.copy(phonePrefix = "1bd"),
                BAD_REQUEST,
                INVALID_PHONE_NUMBER
            ),
            Arguments.of(
                body.copy(name = "    "),
                BAD_REQUEST,
                INVALID_NAME
            ),
            Arguments.of(
                body.copy(surname = "123412341234123412341234123412341234123412341234123412341234123412341234123412341"),
                BAD_REQUEST,
                INVALID_SURNAME
            ),
        )
    }
}
