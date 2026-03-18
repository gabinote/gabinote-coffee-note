package com.gabinote.coffeenote.user.dto.user.controller

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.util.*

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class UserMinimalResControllerDto(
    val uid: UUID,
    val nickname: String,
    val profileImg: String,

    )