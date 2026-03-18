package com.gabinote.coffeenote.user.event.userWithdraw

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserWithdrawEvent(
    val uid: UUID,
)