package com.gabinote.coffeenote.user.dto.user.service

import java.util.*

data class UserRegisterReqServiceDto(
    val uid: UUID,
    val nickname: String,
    val profileImg: String,

    @JvmField
    val isOpenProfile: Boolean,

    @JvmField
    val isMarketingEmailAgreed: Boolean,

    @JvmField
    val isMarketingPushAgreed: Boolean,

    @JvmField
    val isNightPushAgreed: Boolean,

    )