package com.gabinote.coffeenote.user.dto.user.service

import org.bson.types.ObjectId
import java.time.LocalDateTime
import java.util.*

data class UserResServiceDto(
    val id: ObjectId,
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