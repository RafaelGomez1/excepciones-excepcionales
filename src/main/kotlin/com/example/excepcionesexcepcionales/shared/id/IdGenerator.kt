package com.example.excepcionesexcepcionales.shared.id

import java.util.UUID

interface IdGenerator {
    fun generate(): UUID
}

class UUIDGenerator : IdGenerator {
    override fun generate(): UUID = UUID.randomUUID()
}
