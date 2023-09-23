package com.example.excepcionesexcepcionales.user.application.verify.imperative

import com.example.excepcionesexcepcionales.shared.event.DomainEventPublisher
import com.example.excepcionesexcepcionales.user.application.find.UserFinder
import com.example.excepcionesexcepcionales.user.domain.UserId
import com.example.excepcionesexcepcionales.user.domain.UserRepository

class ImperativeUserVerifier(
    private val repository: UserRepository,
    private val publisher: DomainEventPublisher
) {
    private val finder = UserFinder(repository)

    fun invoke(id: UserId) {
        val user = finder.invoke(id)
        val verifiedUser = user.verify()

        repository.save(user)
        publisher.publish(verifiedUser.pullEvents())
    }
}
