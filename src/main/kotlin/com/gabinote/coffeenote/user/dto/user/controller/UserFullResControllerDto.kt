package com.gabinote.coffeenote.user.dto.user.controller

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.time.LocalDateTime
import java.util.*

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class UserFullResControllerDto(
    val uid: UUID,
    val nickname: String,
    val profileImg: String,

    @JvmField
    val isOpenProfile: Boolean,
    val createdDate: LocalDateTime,
    val modifiedDate: LocalDateTime,

    @JvmField
    var isMarketingEmailAgreed: Boolean,

    @JvmField
    var isMarketingPushAgreed: Boolean,

    @JvmField
    var isNightPushAgreed: Boolean,
)