package com.gabinote.coffeenote.common.mapping.time

import com.gabinote.coffeenote.common.util.time.TimeProvider
import org.mapstruct.Mapper
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import java.time.LocalDateTime

/**
 * 애플리케이션 전체에서 사용하는 시간 관련 매핑 처리
 */
@Mapper(
    componentModel = "spring"
)
abstract class TimeMapper {
    @Autowired
    lateinit var timeProvider: TimeProvider

    /**
     * Epoch Milliseconds 를 LocalDateTime 으로 변환한다.
     *
     * @see timeProvider
     *
     * @param epochMilli 변환할 Epoch Milliseconds
     * @return 변환된 LocalDateTime
     *
     */
    fun toDateTime(epochMilli: Long): LocalDateTime {
        return Instant.ofEpochMilli(epochMilli).atZone(timeProvider.zoneId()).toLocalDateTime()
    }

    /**
     * LocalDateTime 을 Epoch Milliseconds 로 변환한다.
     * @see timeProvider
     * @param dateTime 변환할 LocalDateTime
     * @return 변환된 Epoch Milliseconds
     */
    fun toEpochMilli(dateTime: LocalDateTime): Long {
        return dateTime.atZone(timeProvider.zoneId()).toInstant().toEpochMilli()
    }
}