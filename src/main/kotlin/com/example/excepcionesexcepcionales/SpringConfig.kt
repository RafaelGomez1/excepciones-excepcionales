package com.example.excepcionesexcepcionales

import com.example.excepcionesexcepcionales.session.user.application.create.CreateUserCommandHandler
import com.example.excepcionesexcepcionales.shared.event.DomainEventPublisher
import com.example.excepcionesexcepcionales.shared.event.InMemoryEventPublisher
import com.example.excepcionesexcepcionales.solution.user.application.create.functional.FunctionalCreateUserCommandHandler
import com.example.excepcionesexcepcionales.solution.user.application.create.imperative.ImperativeCreateUserCommandHandler
import com.example.excepcionesexcepcionales.solution.user.application.create.sealed.SealedCreateUserCommandHandler
import com.example.excepcionesexcepcionales.solution.user.domain.UserRepository
import com.example.excepcionesexcepcionales.solution.user.secondaryadapter.database.H2UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SpringConfig {

    @Bean fun publisher(): DomainEventPublisher = InMemoryEventPublisher()

    @Bean
    fun createUserCommandHandler(
        repository: UserRepository,
        publisher: DomainEventPublisher
    ): CreateUserCommandHandler = CreateUserCommandHandler(repository, publisher)

    @Bean
    fun functionalCreateUserCommandHandler(
        repository: UserRepository,
        publisher: DomainEventPublisher
    ): FunctionalCreateUserCommandHandler = FunctionalCreateUserCommandHandler(repository, publisher)

    @Bean
    fun imperativeCreateUserCommandHandler(
        repository: UserRepository,
        publisher: DomainEventPublisher
    ): ImperativeCreateUserCommandHandler = ImperativeCreateUserCommandHandler(repository, publisher)

    @Bean
    fun sealedCreateUserCommandHandler(
        repository: UserRepository,
        publisher: DomainEventPublisher
    ): SealedCreateUserCommandHandler = SealedCreateUserCommandHandler(repository, publisher)
}
