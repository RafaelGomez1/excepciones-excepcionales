package com.example.excepcionesexcepcionales.solution.user.secondaryadapter.database

import com.example.excepcionesexcepcionales.solution.user.domain.CardStatus
import com.example.excepcionesexcepcionales.solution.user.domain.Email
import com.example.excepcionesexcepcionales.solution.user.domain.Name
import com.example.excepcionesexcepcionales.solution.user.domain.PhoneNumber
import com.example.excepcionesexcepcionales.solution.user.domain.Status
import com.example.excepcionesexcepcionales.solution.user.domain.Surname
import com.example.excepcionesexcepcionales.solution.user.domain.SolutionUser
import com.example.excepcionesexcepcionales.solution.user.domain.UserId
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.ZonedDateTime
import java.util.UUID

@Entity
@Table
data class JpaUser(
    @Id
    val id: UUID,
    val email: String,
    val phoneNumber: String,
    val phonePrefix: String,
    val createdOn: ZonedDateTime,
    val name: String,
    val surname: String,
    val status: String,
    val cardStatus: String
) {
    fun toSolutionDomain(): SolutionUser = SolutionUser(
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
}

fun SolutionUser.toJpa() = JpaUser(
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
