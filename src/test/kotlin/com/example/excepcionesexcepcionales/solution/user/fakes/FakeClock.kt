package com.example.excepcionesexcepcionales.solution.user.fakes

import com.example.excepcionesexcepcionales.shared.clock.Clock
import java.time.ZonedDateTime

object FakeClock : Clock {

    override fun now(): ZonedDateTime {
        TODO("Not yet implemented")
    }
}
