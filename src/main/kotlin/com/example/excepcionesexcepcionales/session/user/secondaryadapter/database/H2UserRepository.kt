package com.example.excepcionesexcepcionales.session.user.secondaryadapter.database

import com.example.excepcionesexcepcionales.session.user.domain.CardStatus
import com.example.excepcionesexcepcionales.session.user.domain.Email
import com.example.excepcionesexcepcionales.session.user.domain.Name
import com.example.excepcionesexcepcionales.session.user.domain.PhoneNumber
import com.example.excepcionesexcepcionales.session.user.domain.Status
import com.example.excepcionesexcepcionales.session.user.domain.Surname
import com.example.excepcionesexcepcionales.session.user.domain.User
import com.example.excepcionesexcepcionales.session.user.domain.UserId
import com.example.excepcionesexcepcionales.session.user.domain.UserRepository
import com.example.excepcionesexcepcionales.solution.user.secondaryadapter.database.JpaUser
import com.example.excepcionesexcepcionales.solution.user.secondaryadapter.database.JpaUserRepository
import org.springframework.stereotype.Component

@Component
class H2UserRepository(private val jpaRepository: JpaUserRepository) : UserRepository {
    override fun findBy(userId: UserId): User =
        jpaRepository.findById(userId.value)
            .map { it.toDomain() }
            .orElseThrow()

    override fun existBy(userId: UserId): Boolean = jpaRepository.existsById(userId.value)

    override fun existBy(email: Email): Boolean = jpaRepository.existsByEmailIgnoreCase(email.value)

    override fun save(user: User) { jpaRepository.save(user.toJpa()) }
}

internal fun JpaUser.toDomain(): User = User(
    id = UserId(value = id),
    email = Email.create(value = email),
    phoneNumber = PhoneNumber.create(
        number = phoneNumber,
        prefix = phonePrefix
    ),
    createdOn = createdOn,
    name = Name.create(name),
    surname = Surname.create(surname),
    documents = setOf(),
    status = Status.valueOf(status),
    cardStatus = CardStatus.valueOf(cardStatus)
)

internal fun User.toJpa() = JpaUser(
    id = id.value,
    email = email.value,
    phoneNumber = phoneNumber.number(),
    phonePrefix = phoneNumber.prefix(),
    createdOn = createdOn,
    name = name.value,
    surname = surname.value,
    status = status.name,
    cardStatus = cardStatus.name
)
