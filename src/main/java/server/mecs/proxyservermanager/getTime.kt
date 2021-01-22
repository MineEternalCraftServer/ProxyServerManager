package server.mecs.proxyservermanager

import java.time.LocalDateTime
import java.time.ZonedDateTime

object getTime {
    fun getDate(): LocalDateTime? {
        return LocalDateTime.now()
    }

    fun getTime(): Long {
        return ZonedDateTime.now().toEpochSecond()
    }
}