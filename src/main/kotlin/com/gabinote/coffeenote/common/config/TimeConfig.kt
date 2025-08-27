package com.gabinote.coffeenote.common.config

import com.gabinote.coffeenote.common.util.time.TimeProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration
class TimeConfig {

    private val clock: Clock = Clock.systemDefaultZone()

    @Bean
    fun timeHelper(): TimeProvider {
        return TimeProvider(clock)
    }

}