package com.example.excepcionesexcepcionales.solution.user.fakes

import com.example.excepcionesexcepcionales.shared.clock.Clock
import java.time.ZonedDateTime

object FakeClock : Clock {
    private val now = mutableListOf<ZonedDateTime>()

    override fun now(): ZonedDateTime = now.removeFirst()
    fun shouldGenerate(date: ZonedDateTime) { now.add(date) }
    fun resetFake() { now.clear() }
}
