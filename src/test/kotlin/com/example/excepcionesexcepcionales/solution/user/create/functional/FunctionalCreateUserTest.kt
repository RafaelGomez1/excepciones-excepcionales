package com.example.excepcionesexcepcionales.solution.user.create.functional

import com.example.excepcionesexcepcionales.solution.user.application.create.functional.FunctionalCreateUserCommandHandler
import com.example.excepcionesexcepcionales.solution.user.fakes.FakeClock
import com.example.excepcionesexcepcionales.solution.user.fakes.FakeDomainEventPublisher
import com.example.excepcionesexcepcionales.solution.user.fakes.FakeIdGenerator
import com.example.excepcionesexcepcionales.solution.user.fakes.FakeUserRepository
import com.example.excepcionesexcepcionales.solution.user.mothers.CreateUserRequestBodyMother
import com.example.excepcionesexcepcionales.solution.user.mothers.UserCreatedEventMother
import com.example.excepcionesexcepcionales.solution.user.mothers.UserMother
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.CreateUserRequestBody
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.functional.FunctionalCreateUserController
import java.util.stream.Stream
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.HttpStatus

class FunctionalCreateUserTest {

    private val repository = FakeUserRepository
    private val publisher = FakeDomainEventPublisher
    private val idGenerator = FakeIdGenerator
    private val clock = FakeClock

    private val handler = FunctionalCreateUserCommandHandler(repository, publisher)

    private val controller = FunctionalCreateUserController(handler, idGenerator, clock)

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
        Assertions.assertEquals(HttpStatus.CREATED, result.statusCode)
        Assertions.assertTrue { repository.wasPersisted(user) }
        Assertions.assertTrue { publisher.wasPublished(expectedEvent) }
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
        Assertions.assertEquals(HttpStatus.CONFLICT, result.statusCode)
        Assertions.assertEquals(UserServerErrors.USER_ALREADY_EXISTS, result.body)
        Assertions.assertFalse { publisher.wasPublished(expectedEvent) }
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
        Assertions.assertEquals(status, result.statusCode)
        Assertions.assertEquals(error, result.body)
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
                HttpStatus.BAD_REQUEST,
                UserServerErrors.INVALID_EMAIL
            ),
            Arguments.of(
                body.copy(phoneNumber = "+3a1"),
                HttpStatus.BAD_REQUEST,
                UserServerErrors.INVALID_PHONE_NUMBER
            ),
            Arguments.of(
                body.copy(phonePrefix = "1bd"),
                HttpStatus.BAD_REQUEST,
                UserServerErrors.INVALID_PHONE_NUMBER
            ),
            Arguments.of(
                body.copy(name = "    "),
                HttpStatus.BAD_REQUEST,
                UserServerErrors.INVALID_NAME
            ),
            Arguments.of(
                body.copy(surname = "123412341234123412341234123412341234123412341234123412341234123412341234123412341"),
                HttpStatus.BAD_REQUEST,
                UserServerErrors.INVALID_SURNAME
            ),
        )
    }
}
