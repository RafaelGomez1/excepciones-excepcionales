package com.example.excepcionesexcepcionales.solution.user.fakes

import com.example.excepcionesexcepcionales.shared.id.IdGenerator
import java.util.UUID

object FakeIdGenerator : IdGenerator {
    private val ids = mutableListOf<UUID>()

    fun shouldGenerate(vararg generatedIds: UUID) { generatedIds.forEach { ids.add(it) } }
    fun resetFake() { ids.clear() }

    override fun generate(): UUID = ids.removeFirst()
}
