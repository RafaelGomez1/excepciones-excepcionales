package com.example.excepcionesexcepcionales.user.domain

import com.example.excepcionesexcepcionales.shared.event.Aggregate
import com.example.excepcionesexcepcionales.shared.event.DomainEvent
import com.example.excepcionesexcepcionales.shared.event.DomainEvent.UserVerifiedEvent
import com.example.excepcionesexcepcionales.user.domain.Status.INCOMPLETE
import com.example.excepcionesexcepcionales.user.domain.Status.PENDING_VERIFICATION
import com.example.excepcionesexcepcionales.user.domain.Status.VERIFIED
import java.time.ZonedDateTime

data class User(
    val id: UserId,
    val email: Email,
    val mobilePhone: MobilePhone,
    val createdOn: ZonedDateTime,
    val name: Name,
    val surname: Surname,
    val documents: Set<Document> = setOf(),
    val status: Status
): Aggregate() {

    fun verify(): User {
        guardAllDocumentsAreVerified()
        guardStatusCanBeVerified()
        return copy(status = VERIFIED)
            .also { it.pushEvent(UserVerifiedEvent(id.toString())) }
    }

    private fun guardAllDocumentsAreVerified() =
        if(!documents.all { document -> document.status == DocumentStatus.VERIFIED })
            throw NotAllDocumentVerifiedException()
        else Unit

    private fun guardStatusCanBeVerified() {
        when(status) {
            INCOMPLETE -> throw UserStatusCannotBeVerifiedException()
            PENDING_VERIFICATION -> Unit
            VERIFIED -> throw UserAlreadyVerifiedException()
        }
    }

    class NotAllDocumentVerifiedException() : Throwable()
    class UserAlreadyVerifiedException() : Throwable()
    class UserStatusCannotBeVerifiedException() : Throwable()
}


