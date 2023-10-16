package com.example.excepcionesexcepcionales.session.user.mothers

import com.example.excepcionesexcepcionales.session.user.domain.User
import com.example.excepcionesexcepcionales.shared.clock.UTCClock
import com.example.excepcionesexcepcionales.session.user.domain.CardStatus
import com.example.excepcionesexcepcionales.session.user.domain.Document
import com.example.excepcionesexcepcionales.session.user.domain.DocumentStatus
import com.example.excepcionesexcepcionales.session.user.domain.DocumentType
import com.example.excepcionesexcepcionales.session.user.domain.Email
import com.example.excepcionesexcepcionales.session.user.domain.Name
import com.example.excepcionesexcepcionales.session.user.domain.PhoneNumber
import com.example.excepcionesexcepcionales.session.user.domain.Status
import com.example.excepcionesexcepcionales.session.user.domain.Surname
import com.example.excepcionesexcepcionales.session.user.domain.UserId
import com.example.excepcionesexcepcionales.session.user.domain.Version
import java.time.ZonedDateTime

object UserMother {
    fun random(
        id: UserId = UserIdMother.random(),
        email: Email = EmailMother.random(),
        phoneNumber: PhoneNumber = PhoneNumberMother.random(),
        createdOn: ZonedDateTime = UTCClock().now(),
        name: Name = NameMother.random(),
        surname: Surname = SurnameMother.random(),
        documents: Set<Document> = setOf(DocumentMother.createValidDocument()),
        status: Status = Status.values().random(),
        cardStatus: CardStatus = CardStatus.values().random()
    ): User =
        User(
            id = id,
            email = email,
            phoneNumber = phoneNumber,
            createdOn = createdOn,
            name = name,
            surname = surname,
            documents = documents,
            status = status,
            cardStatus = cardStatus
        )

    fun incomplete(): User =
        random(
            documents = emptySet(),
            cardStatus = CardStatus.PENDING,
            status = Status.INCOMPLETE
        )
}

object DocumentMother {
    fun createValidDocument(): Document {
        return Document(
            type = DocumentType.values().random(),
            version = Version("1"),
            status = DocumentStatus.values().random()
        )
    }

    fun createDocumentWithStatus(status: DocumentStatus): Document {
        return createValidDocument().copy(status = status)
    }
}

