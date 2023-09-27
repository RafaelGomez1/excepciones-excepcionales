package com.example.excepcionesexcepcionales.shared.clock

import java.time.ZoneId
import java.time.ZonedDateTime

interface Clock {
    fun now(): ZonedDateTime
}

class UTCClock : Clock {
    override fun now(): ZonedDateTime = ZonedDateTime.now(ZoneId.of("UTC"))
}
