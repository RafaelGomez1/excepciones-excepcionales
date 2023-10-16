package com.example.excepcionesexcepcionales.session.user.application.verify

import com.example.excepcionesexcepcionales.session.user.application.find.UserFinder
import com.example.excepcionesexcepcionales.session.user.domain.UserRepository
import com.example.excepcionesexcepcionales.shared.event.DomainEventPublisher
import com.example.excepcionesexcepcionales.session.user.domain.UserId

class UserVerifier(
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
