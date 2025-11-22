package com.gabinote.coffeenote.testSupport.testUtil.time

import com.gabinote.coffeenote.common.util.time.TimeProvider
import java.time.LocalDate
import java.time.LocalDateTime

class TestTimeProvider : TimeProvider {
    companion object {
        val testDateTime = LocalDateTime.of(2002, 8, 28, 0, 0)
        val testDate = testDateTime.toLocalDate()
    }

    override fun now(): LocalDateTime {
        return testDateTime
    }

    override fun today(): LocalDate {
        return testDate
    }
}